package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.resource.AtomResource;
import eu.icarus.momca.momcapi.model.resource.ExistResource;
import eu.icarus.momca.momcapi.model.resource.MyCollection;
import eu.icarus.momca.momcapi.model.xml.atom.AtomAuthor;
import eu.icarus.momca.momcapi.model.xml.atom.AtomEntry;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.model.xml.xrx.Keywords;
import eu.icarus.momca.momcapi.query.ExistQuery;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A connection to a MOM-CA database instance. Provides access to all manager classes.
 *
 * @author Daniel Jeller
 *         Created on 24.06.2015.
 */
public class ExistMomcaConnection implements MomcaConnection {

    @NotNull
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExistMomcaConnection.class);
    @NotNull
    private final String admin;
    @NotNull
    private final ArchiveManager archiveManager;
    @NotNull
    private final CharterManager charterManager;
    @NotNull
    private final CollectionManager collectionManager;
    @NotNull
    private final CountryManager countryManager;
    @NotNull
    private final String dbRootUri;
    @NotNull
    private final FondManager fondManager;
    @NotNull
    private final MyCollectionManager myCollectionManager;
    @NotNull
    private final String password;
    @NotNull
    private final Collection rootCollection;
    @NotNull
    private final ExistUserManager userManager;

    public ExistMomcaConnection(@NotNull String dbRootUri, @NotNull String admin, @NotNull String password) {


        LOGGER.debug("Initiating connection to '{}' for user '{}'.", dbRootUri, admin);

        this.dbRootUri = dbRootUri;
        this.admin = admin;
        this.password = password;

        rootCollection = initDatabaseConnection();

        userManager = new ExistUserManager(this, rootCollection);
        countryManager = new ExistCountryManager(this);
        archiveManager = new ExistArchiveManager(this);
        fondManager = new ExistFondManager(this);
        collectionManager = new ExistCollectionManager(this);
        charterManager = new ExistCharterManager(this);
        myCollectionManager = new ExistMyCollectionManager(this);

        LOGGER.info("Connection to '{}' for user '{}' established.", dbRootUri, admin);

    }

    @Override
    public boolean closeConnection() {

        boolean success = false;
        LOGGER.debug("Trying to close connection to '{}'.", dbRootUri);

        try {

            rootCollection.close();
            success = true;
            LOGGER.info("Connection to '{}' closed.", dbRootUri);

        } catch (XMLDBException e) {
            LOGGER.error("Failed to close the database connection.", e);
        }

        return success;

    }

    boolean createCollection(@NotNull String name, @NotNull String parentUri) {

        LOGGER.debug("Trying to create eXist-collection '{}/{}'.", parentUri, name);

        String encodedName = Util.encode(name);
        String encodedParentUri = Util.encode(parentUri);

        ExistQuery query = ExistQueryFactory.createCollection(encodedParentUri, encodedName);
        List<String> result = queryDatabase(query);

        String resultingCollectionUri = encodedParentUri + "/" + encodedName;

        boolean success = result.size() == 1 && result.get(0).equals(resultingCollectionUri);

        if (success) {
            LOGGER.debug("EXist-collection '{}' created.", resultingCollectionUri);
        } else {
            LOGGER.debug("Failed to create eXist-collection '{}'.", resultingCollectionUri);
        }

        return success;

    }

    boolean deleteCollection(@NotNull String uri) {

        LOGGER.debug("Trying to delete eXist-collection '{}'.", uri);

        boolean success = false;

        if (isCollectionExisting(uri)) {

            ExistQuery query = ExistQueryFactory.removeCollection(Util.encode(uri));
            success = Util.isTrue(queryDatabase(query));

            if (success) {
                LOGGER.debug("EXist-collection '{}' deleted.", uri);
            } else {
                LOGGER.debug("Failed to delete eXist-collection '{}'", uri);
            }

        } else {
            LOGGER.debug("EXist-collection '{}' not existing. Aborting deletion.", uri);
        }

        return success;

    }

    boolean deleteExistResource(@NotNull ExistResource resource) {

        String uri = resource.getUri();
        LOGGER.debug("Trying to delete eXist-resource '{}'.", uri);

        boolean success = false;

        if (isResourceExisting(uri)) {

            ExistQuery query = ExistQueryFactory.removeResource(resource);
            success = Util.isTrue(queryDatabase(query));

            if (success) {
                LOGGER.debug("R" +
                        "EXist-resource '{}' deleted.", uri);
            } else {
                LOGGER.debug("Failed to delete eXist-resource '{}'", uri);
            }

        } else {
            LOGGER.debug("EXist-resource '{}' not existing. Aborting deletion.", uri);
        }

        return success;

    }

    @Override
    @NotNull
    public ArchiveManager getArchiveManager() {
        return archiveManager;
    }

    @Override
    @NotNull
    public CharterManager getCharterManager() {
        return charterManager;
    }

    @Override
    @NotNull
    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    @Override
    @NotNull
    public CountryManager getCountryManager() {
        return countryManager;
    }

    @Override
    @NotNull
    public FondManager getFondManager() {
        return fondManager;
    }

    @Override
    @NotNull
    public MyCollectionManager getMyCollectionManager() {
        return myCollectionManager;
    }

    @Override
    @NotNull
    public ExistUserManager getUserManager() {
        return userManager;
    }

    private Collection initDatabaseConnection() {

        try {

            org.xmldb.api.base.Database dbDatabase = (org.xmldb.api.base.Database) Class.forName(DRIVER).newInstance();
            DatabaseManager.registerDatabase(dbDatabase);
            return readCollection("/db").get();

        } catch (@NotNull ClassNotFoundException | IllegalAccessException | InstantiationException e) {

            throw new MomcaException("Failed to initialize database connection.", e);

        } catch (XMLDBException e) {

            if (e.getMessage().equals("Wrong password for user [admin] ")) {
                throw new MomcaException("Wrong admin password!", e);
            } else {
                throw new MomcaException(String.format("Failed to connect to remote database '%s'", dbRootUri), e);
            }

        }

    }

    boolean isCollectionExisting(@NotNull String collectionUri) {

        LOGGER.debug("Testing existence of eXist-collection '{}'.", collectionUri);

        String encodedUri = Util.encode(collectionUri);
        ExistQuery query = ExistQueryFactory.checkCollectionExistence(encodedUri);
        boolean isExisting = Util.isTrue(queryDatabase(query));

        LOGGER.debug("Returning '{}' for the existence of eXist-collection '{}'.", isExisting, collectionUri);

        return isExisting;

    }

    boolean isResourceExisting(@NotNull String resourceUri) {

        LOGGER.debug("Testing existence of eXist-resource '{}'.", resourceUri);

        ExistQuery query = ExistQueryFactory.checkExistResourceExistence(resourceUri);
        boolean isExisting = Util.isTrue(queryDatabase(query));

        LOGGER.debug("Returning '{}' for the existence of eXist-resource '{}'.", isExisting, resourceUri);

        return isExisting;

    }

    boolean makeSureCollectionPathExists(@NotNull String absoluteUri) {

        LOGGER.debug("Trying to create all eXist-collections on path '{}'.", absoluteUri);

        boolean success = false;

        if (!absoluteUri.isEmpty() && absoluteUri.startsWith("/db/")) {

            if (isCollectionExisting(absoluteUri)) {

                success = true;
                LOGGER.debug("EXist-collection '{}' already exists. Aborting creation.", absoluteUri);

            } else {

                String[] parts = absoluteUri.replace("/db/", "").split("/");
                String parentUri = "/db";

                for (String part : parts) {

                    LOGGER.debug("Trying to add eXist-collection '{}' in '{}'", part, parentUri);

                    success = createCollection(part, parentUri);
                    parentUri = parentUri + "/" + part;

                    if (success) {
                        LOGGER.debug("Successfully added eXist-collection '{}' in '{}'", part, parentUri);
                    } else {
                        LOGGER.debug("Failed to create eXist-collection '{}' on path '{}'. Aborting further creation attempts.", part, absoluteUri);
                        break;
                    }

                }

                if (success) {
                    LOGGER.debug("Created all eXist-collections on path '{}'.", absoluteUri);
                } else {
                    LOGGER.debug("Failed to create all eXist-collections on path '{}'", absoluteUri);
                }

            }

        } else {
            LOGGER.debug("Creation of eXist-collection path '{}' aborted. Path invalid.", absoluteUri);
        }

        return success;

    }

    @NotNull
    List<String> queryDatabase(@NotNull ExistQuery existQuery) {

        LOGGER.debug("Trying to execute query: {}", existQuery.getQuery());

        List<String> resultList = new ArrayList<>(0);

        XPathQueryService queryService = null;
        try {
            queryService = (XPathQueryService) rootCollection.getService("XPathQueryService", "1.0");
        } catch (XMLDBException e) {
            LOGGER.error("Failed to get the XPath query service. Aborting querying.", e);
        }

        if (queryService != null) {

            ResourceSet resultSet = null;
            try {
                resultSet = queryService.query(existQuery.getQuery());
            } catch (XMLDBException e) {
                LOGGER.error("Failed to execute query: {}.", existQuery.getQuery(), e);
            }

            if (resultSet != null) {

                LOGGER.trace("Trying to extract results from query resultSet.");

                try {

                    ResourceIterator iterator = resultSet.getIterator();
                    while (iterator.hasMoreResources()) {
                        XMLResource resource = (XMLResource) iterator.nextResource();
                        resultList.add(resource.getContent().toString());
                    }

                    LOGGER.debug("Returning {} query results.", resultList.size());
                    LOGGER.trace("Results: {}", resultList);

                } catch (XMLDBException e) {
                    LOGGER.error("Failed to extract query results from query resultSet.", e);
                }

            }

        }

        return resultList;

    }

    String queryRemoteDateTime() {

        LOGGER.debug("Trying to query current time from the server.");

        String time = queryDatabase(ExistQueryFactory.getCurrentDateTime()).get(0);

        LOGGER.debug("Time at server is currently '{}'.", time);

        return time;

    }

    @NotNull
    Optional<Collection> readCollection(@NotNull String uri) {

        LOGGER.debug("Trying to read eXist-collection '{}' from the database.", uri);

        Collection result = null;

        try {

            String absoluteUri = dbRootUri + Util.encode(uri);
            result = DatabaseManager.getCollection(absoluteUri, admin, password);

            if (result == null) {
                LOGGER.debug("EXist-collection '{}' not found in the database. Returning nothing.", uri);
            } else {
                LOGGER.debug("EXist-collection '{}' read from the database.", uri);
            }


        } catch (@NotNull XMLDBException e) {
            LOGGER.error("Failed to get eXist-collection '{}' from the database due to an XMLDBException. Returning nothing.",
                    uri, e);
        }

        return Optional.ofNullable(result);

    }

    @NotNull
    Optional<ExistResource> readExistResource(@NotNull String resourceUri) {

        LOGGER.debug("Trying to read eXist-resource '{}' from the database.", resourceUri);

        ExistResource resource = null;

        String encodedUri = Util.encode(resourceUri);
        ExistQuery query = ExistQueryFactory.getResource(encodedUri);
        List<String> result = queryDatabase(query);

        if (result.size() == 1 && !result.get(0).isEmpty()) {

            String name = Util.getLastUriPart(resourceUri);
            String parentUri = Util.getParentUri(resourceUri);
            String content = result.get(0);

            resource = new ExistResource(name, parentUri, content);

        }

        if (resource == null) {
            LOGGER.debug("Failed to read eXist-resource '{}' from the database.", resourceUri);
        } else {
            LOGGER.debug("EXist-resource '{}' read from the database: {}", resourceUri, resource);
        }

        return Optional.ofNullable(resource);

    }

    boolean writeAtomResource(@NotNull AtomResource resource,
                              @NotNull String publishedDateTime, @NotNull String updatedDateTime) {

        LOGGER.debug("Trying to write atom resource '{}' to the database.", resource.getId());

        AtomId id = resource.getId().getContentAsElement();
        AtomAuthor author = resource.getCreator().map(IdUser::getContentAsElement).orElse(new AtomAuthor(""));
        Element content = resource.getContent();

        AtomEntry newEntry = new AtomEntry(id, author, publishedDateTime, updatedDateTime, content);

        LOGGER.trace("Atom entry '{}' created.", newEntry);

        if (resource instanceof eu.icarus.momca.momcapi.model.resource.Collection) {
            eu.icarus.momca.momcapi.model.resource.Collection coll =
                    (eu.icarus.momca.momcapi.model.resource.Collection) resource;
            coll.getKeyword().ifPresent(s -> newEntry.insertChild(new Keywords(s), 6));
            LOGGER.trace("Keywords inserted into MOM-CA collection XML.");
        }

        if (resource instanceof MyCollection) {
            MyCollection myColl = (MyCollection) resource;
            newEntry.insertChild(myColl.getSharing(), 6);
            LOGGER.trace("Sharing data inserted into MyCollection XML.");
        }

        String resourceName = resource.getResourceName();
        String parentUri = resource.getParentUri();

        ExistResource updatedResource = new ExistResource(resourceName, parentUri, newEntry.toXML());

        boolean success = writeExistResource(updatedResource);

        if (success) {
            LOGGER.debug("Atom resource '{}' written to the database.", resource);
        } else {
            LOGGER.debug("Failed to write atom resource '{}' to the database.", resource);
        }

        return success;

    }

    boolean writeExistResource(@NotNull ExistResource resource) {

        String resourceUri = resource.getUri();

        LOGGER.debug("Trying to write eXist-resource '{}' to the database.", resourceUri);

        ExistQuery query = ExistQueryFactory.storeResource(resource);
        List<String> result = queryDatabase(query);

        boolean success = result.size() == 1 && result.get(0).equals(resourceUri);

        if (success) {
            LOGGER.debug("EXist-resource '{}' written to the database.", resourceUri);
        } else {
            LOGGER.debug("Failed to write eXist-resource '{}' to the database", resourceUri);
        }

        return success;

    }

}