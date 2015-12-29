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
 * An implementation of <code>MomcaConnection</code> based on eXist.
 */
class ExistMomcaConnection implements MomcaConnection {

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

    /**
     * Creates a MomcaConnection instance and establishes the connection to an MOM-CA database instance based on eXist.
     *
     * @param xmlrpcUri The uri to reach the xmlrpc of the database instance, e.g. <code>xmldb:exist://192.168.56.10:8181/xmlrpc</code>.
     * @param admin     The admin username.
     * @param password  The admin password.
     */
    public ExistMomcaConnection(@NotNull String xmlrpcUri, @NotNull String admin, @NotNull String password) {

        LOGGER.debug("Initiating connection to '{}' for user '{}'.", xmlrpcUri, admin);

        this.dbRootUri = xmlrpcUri;
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

        LOGGER.info("Connection to '{}' for user '{}' established.", xmlrpcUri, admin);

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

    /**
     * Creates an eXist-collection in the database.
     *
     * @param name      The name of the eXist-collection.
     * @param parentUri The URI of the parent-collection.
     * @return <code>True</code> if the action was successful.
     */
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

    /**
     * Creates all eXist-collections that lie on an URI.
     *
     * @param absoluteUri The absolute URI to create, e.g. <code>/db/create/all/collections/on/uri</code>.
     * @return <code>True</code> if the eXist-collection path already exists or the creation was successful.
     */
    boolean createCollectionPath(@NotNull String absoluteUri) {

        LOGGER.debug("Trying to create all eXist-collections on path '{}'.", absoluteUri);

        boolean proceed = true;

        if (absoluteUri.isEmpty() || !absoluteUri.startsWith("/db/")) {
            proceed = false;
            LOGGER.debug("Creation of eXist-collection path '{}' aborted. Path invalid.", absoluteUri);
        }

        boolean success = false;

        if (proceed) {

            ExistQuery query = ExistQueryFactory.createCollectionPath(absoluteUri);

            List<String> results = queryDatabase(query);

            if (results.size() == 1 && results.get(0).equals("true")) {

                success = true;
                LOGGER.info("Path '{}' is already existing.", absoluteUri);

            } else if (results.size() != 0 && results.get(results.size() - 1).equals(absoluteUri)) {

                success = true;
                LOGGER.info("Created path '{}'.", absoluteUri);

            } else {

                LOGGER.info("Failed to create path '{}'.", absoluteUri);

            }

        }

        return success;

    }

    /**
     * Deletes an eXist-collection from the database.
     *
     * @param uri The URI of the collection to delete.
     * @return <code>True</code> if the action was successful.
     */
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

    /**
     * Deletes an eXist-resource from the database.
     *
     * @param resource The eXist-resource to delete.
     * @return <code>True</code> if the process was successful.
     */
    boolean deleteResource(@NotNull ExistResource resource) {

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

    /**
     * Checks if an eXist-collection is existing in the database.
     *
     * @param uri The URI of the eXist-collection.
     * @return <code>True</code> if the eXist-collection exists.
     */
    boolean isCollectionExisting(@NotNull String uri) {

        LOGGER.debug("Testing existence of eXist-collection '{}'.", uri);

        String encodedUri = Util.encode(uri);
        ExistQuery query = ExistQueryFactory.checkCollectionExistence(encodedUri);
        boolean isExisting = Util.isTrue(queryDatabase(query));

        LOGGER.debug("Returning '{}' for the existence of eXist-collection '{}'.", isExisting, uri);

        return isExisting;

    }

    /**
     * Checks if an eXist-resource is existing in the database.
     *
     * @param uri The URI of the eXist-resource.
     * @return <code>True</code> if the eXist-resource exists.
     */
    boolean isResourceExisting(@NotNull String uri) {

        LOGGER.debug("Testing existence of eXist-resource '{}'.", uri);

        ExistQuery query = ExistQueryFactory.checkExistResourceExistence(uri);
        boolean isExisting = Util.isTrue(queryDatabase(query));

        LOGGER.debug("Returning '{}' for the existence of eXist-resource '{}'.", isExisting, uri);

        return isExisting;

    }

    /**
     * Executes an ExistQuery on the database.
     *
     * @param existQuery The query to execute.
     * @return A list with all results returned by the database. The exact contents depend on the specific query.
     */
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

    /**
     * Gets the current time from the server.
     *
     * @return The time as an ISO 8601, e.g. <code>2011-05-30T20:31:19.638+02:00</code>
     * @see <a href="http://www.w3.org/TR/NOTE-datetime">W3C Date and Time Formats</a>
     */
    String queryRemoteDateTime() {

        LOGGER.debug("Trying to query current time from the server.");

        String time = queryDatabase(ExistQueryFactory.getCurrentDateTime()).get(0);

        LOGGER.debug("Time at server is currently '{}'.", time);

        return time;

    }

    /**
     * Reads a eXist-collection from the database.
     *
     * @param uri The URI of the eXist-collection.
     * @return The eXist-collection wrapped in an Optional.
     */
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

    /**
     * Reads an eXist-resource from the database.
     *
     * @param uri The URI of the resource.
     * @return The eXist-resource wrapped in an Optional.
     */
    @NotNull
    Optional<ExistResource> readExistResource(@NotNull String uri) {

        LOGGER.debug("Trying to read eXist-resource '{}' from the database.", uri);

        ExistResource resource = null;

        String encodedUri = Util.encode(uri);
        ExistQuery query = ExistQueryFactory.getResource(encodedUri);
        List<String> result = queryDatabase(query);

        if (result.size() == 1 && !result.get(0).isEmpty()) {

            String name = Util.getLastUriPart(uri);
            String parentUri = Util.getParentUri(uri);
            String content = result.get(0);

            resource = new ExistResource(name, parentUri, content);

        }

        if (resource == null) {
            LOGGER.debug("Failed to read eXist-resource '{}' from the database.", uri);
        } else {
            LOGGER.debug("EXist-resource '{}' read from the database: {}", uri, resource);
        }

        return Optional.ofNullable(resource);

    }

    /**
     * Writes an Atom-resource to the database. Updates the published and updated times.
     *
     * @param resource          The Atom-resource to write.
     * @param publishedDateTime The time the resource was first published.
     * @param updatedDateTime   The time the resource was updated.
     * @return <code>True</code> if the action was successful.
     */
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

    /**
     * Writes an eXist-resource to the database.
     *
     * @param resource The resource to write.
     * @return <code>True</code> if the action was successful.
     */
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