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
import org.exist.xmldb.RemoteCollection;
import org.exist.xmldb.RemoteCollectionManagementService;
import org.exist.xmldb.RemoteUserManagementService;
import org.exist.xmldb.UserManagementService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.CollectionManagementService;
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
public class MomcaConnection {

    @NotNull
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final Logger LOGGER = LoggerFactory.getLogger(MomcaConnection.class);
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
    private final UserManager userManager;

    public MomcaConnection(@NotNull String dbRootUri, @NotNull String admin, @NotNull String password) {


        LOGGER.debug("Initiating connection to '{}' for user '{}'.", dbRootUri, admin);

        this.dbRootUri = dbRootUri;
        this.admin = admin;
        this.password = password;

        rootCollection = initDatabaseConnection();

        userManager = new UserManager(this, rootCollection);
        countryManager = new CountryManager(this);
        archiveManager = new ArchiveManager(this);
        fondManager = new FondManager(this);
        collectionManager = new CollectionManager(this);
        charterManager = new CharterManager(this);
        myCollectionManager = new MyCollectionManager(this);

        LOGGER.info("Connection to '{}' for user '{}' established.", dbRootUri, admin);

    }

    public void closeConnection() {

        LOGGER.debug("Trying to close connection to '{}'.", dbRootUri);

        try {

            rootCollection.close();
            LOGGER.info("Connection to '{}' closed.", dbRootUri);

        } catch (XMLDBException e) {
            LOGGER.error("Failed to close the database connection.", e);
        }

    }

    boolean createCollectionPath(@NotNull String absoluteUri) {

        LOGGER.debug("Trying to create all collections on path '{}'.", absoluteUri);

        boolean success = false;

        if (!absoluteUri.isEmpty() && absoluteUri.startsWith("/db/")) {

            absoluteUri = absoluteUri.replace("/db/", "");

            Collection parent = rootCollection;

            String[] parts = absoluteUri.split("/");

            for (String part : parts) {

                try {

                    CollectionManagementService parentService =
                            (RemoteCollectionManagementService) parent.getService("CollectionManagementService", "1.0");

                    parentService.createCollection(part);

                    LOGGER.debug("Collection '{}/{}' created.", parent.getName(), part);

                    parent = parent.getChildCollection(part);

                } catch (XMLDBException e) {
                    LOGGER.error("XMLDBException while trying to add collection '{}/{}'. Aborting creation.", parent.toString(), part, e);
                    break;
                }

            }

            LOGGER.debug("Created all collections on path '{}'.", absoluteUri);
            success = true;

        } else {
            LOGGER.debug("Creation of path '{}' aborted. Path invalid.", absoluteUri);
        }

        return success;

    }

    boolean deleteCollection(@NotNull String uri) {

        boolean success = false;

        LOGGER.debug("Trying to delete collection '{}'.", uri);

        Optional<Collection> collectionOptional = readCollection(uri);

        if (collectionOptional.isPresent()) {

            Collection collection = collectionOptional.get();

            try {

                RemoteCollectionManagementService service = (RemoteCollectionManagementService) collection
                        .getParentCollection().getService("CollectionManagementService", "1.0");
                service.removeCollection(((RemoteCollection) collection).getPathURI());

                success = true;

                LOGGER.debug("Collection '{}' deleted.", uri);

            } catch (XMLDBException e) {
                LOGGER.error("Failed to delete '{}' due to an XMLDBException.", uri, e);
            }

        } else {
            LOGGER.debug("Collection '{}' not existing, nothing deleted.", uri);
        }

        return success;

    }

    boolean deleteExistResource(@NotNull ExistResource resourceToDelete) {

        boolean success = false;

        LOGGER.debug("Trying to delete resource '{}'", resourceToDelete);

        Optional<Collection> collectionOptional = readCollection(resourceToDelete.getParentUri());

        if (collectionOptional.isPresent()) {

            Collection collection = collectionOptional.get();

            try {

                String path = findMatchingResource(resourceToDelete.getResourceName(), collection.listResources());
                Resource res = collection.getResource(path);

                if (res != null) {

                    collection.removeResource(res);

                    success = true;

                    LOGGER.debug("Resource '{}' deleted.", resourceToDelete);

                }

            } catch (@NotNull XMLDBException e) {
                LOGGER.error("Failed to delete '{}' due to an XMLDBException.", resourceToDelete, e);
            }

        } else {
            LOGGER.debug("Failed to locate parent collection of resource '{}', nothing deleted.", resourceToDelete);
        }

        return success;

    }

    @NotNull
    private String findMatchingResource(@NotNull String resourceName, @NotNull String[] resources) {

        String matchingName = "";

        for (String resource : resources) {

            if (Util.decode(resource).equals(Util.decode(resourceName))) {
                matchingName = resource;
            }

        }

        return matchingName;

    }

    @NotNull
    public ArchiveManager getArchiveManager() {
        return archiveManager;
    }

    @NotNull
    public CharterManager getCharterManager() {
        return charterManager;
    }

    @NotNull
    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    @NotNull
    public CountryManager getCountryManager() {
        return countryManager;
    }

    @NotNull
    public FondManager getFondManager() {
        return fondManager;
    }

    @NotNull
    public MyCollectionManager getMyCollectionManager() {
        return myCollectionManager;
    }

    @NotNull
    public UserManager getUserManager() {
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

                    LOGGER.debug("Returning {} query results", resultList.size());

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

        LOGGER.debug("Trying to read collection '{}' from the database.", uri);

        Collection result = null;

        try {

            result = DatabaseManager.getCollection(dbRootUri + Util.encode(uri), admin, password);

            if (result == null) {
                LOGGER.debug("Collection '{}' not found in the database. Returning nothing.", uri);
            } else {
                LOGGER.debug("Collection '{}' read from the database.", uri);
            }


        } catch (@NotNull XMLDBException e) {
            LOGGER.error("Failed to get collection '{}' from the database due to an XMLDBException. Returning nothing.",
                    uri, e);
        }

        return Optional.ofNullable(result);

    }

    @NotNull
    Optional<ExistResource> readExistResource(@NotNull String resourceName, @NotNull String parentCollectionPath) {

        LOGGER.debug("Trying to read resource '{}/{}' from the database.", parentCollectionPath, resourceName);

        ExistResource result = null;

        Optional<Collection> parentCollection = readCollection(parentCollectionPath);

        if (parentCollection.isPresent()) {

            try {

                Collection collection = parentCollection.get();

                String resourcePath = findMatchingResource(resourceName, collection.listResources());
                XMLResource resource = (XMLResource) collection.getResource(resourcePath);

                if (resource == null) {

                    LOGGER.debug("Resource '{}/{}' not found in the database. Returning nothing.",
                            parentCollectionPath, resourceName);

                } else {

                    String content = (String) resource.getContent();
                    result = new ExistResource(resourceName, parentCollectionPath, content);
                    LOGGER.debug("Resource '{}' read from the database.", result);

                }

            } catch (XMLDBException e) {
                LOGGER.error("Encountered XMLDBException on trying to get resource '{}' from collection '{}'." +
                        " Returned 'Optional.empty'", resourceName, parentCollectionPath);
            }

        } else {
            LOGGER.debug("Parent collection of resource '{}/{}' not found in the database. Returning nothing.",
                    resourceName, parentCollectionPath);
        }

        return Optional.ofNullable(result);

    }

    boolean writeAtomResource(@NotNull AtomResource resource,
                              @NotNull String publishedDateTime, @NotNull String updatedDateTime) {

        LOGGER.debug("Trying to write atom resource '{}' to the database.", resource.getId());

        AtomId id = resource.getId().getContentXml();
        AtomAuthor author = resource.getCreator().map(IdUser::getContentXml).orElse(new AtomAuthor(""));
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
            LOGGER.debug("Failed to write resource '{}' to the database.", resource);
        }

        return success;

    }

    boolean writeCollection(@NotNull String name, @NotNull String parentUri) {

        LOGGER.debug("Trying to write collection '{}/{}'.", parentUri, name);

        boolean success = false;

        String encodedName = Util.encode(name);
        String encodedPath = Util.encode(parentUri);

        Optional<Collection> parent = readCollection(encodedPath);

        if (parent.isPresent()) {

            try {

                CollectionManagementService parentService = (RemoteCollectionManagementService) parent.get().getService("CollectionManagementService", "1.0");
                parentService.createCollection(encodedName);

                Collection newCollection = parent.get().getChildCollection(encodedName);
                UserManagementService userService = (RemoteUserManagementService) newCollection.getService("UserManagementService", "1.0");
                userService.chmod("rwxrwxrwx");

                success = true;

                LOGGER.debug("Collection '{}/{}' written.", parentUri, name);

                newCollection.close();

            } catch (XMLDBException e) {
                LOGGER.error("XMLDBException while trying to write collection '{}' to '{}'. Adding failed.", name, parentUri, e);
            }

        } else {
            LOGGER.debug("Failed to write collection '{}' because parent collection '{}' does not exist.", name, parentUri);
        }

        return success;

    }


    boolean writeExistResource(@NotNull ExistResource resource) {

        boolean success = false;

        LOGGER.debug("Trying to write resource '{}' to the database.", resource);

        Optional<Collection> collectionOptional = readCollection(resource.getParentUri());

        if (collectionOptional.isPresent()) {

            Collection collection = collectionOptional.get();

            try {

                XMLResource newResource = (XMLResource) collection.createResource(resource.getResourceName(), "XMLResource");
                newResource.setContent(resource.toXML());
                collection.storeResource(newResource);

                UserManagementService userService = (RemoteUserManagementService) collection.getService("UserManagementService", "1.0");
                userService.chmod(newResource, "rwxrwxrwx");

                collection.close();

                success = true;

                LOGGER.debug("Resource '{}' written to the database.", resource);


            } catch (XMLDBException e) {
                throw new MomcaException("Failed to create new resource.", e);
            }

        } else {
            LOGGER.debug("Failed to locate parent collection for resource '{}'. Aborting write.", resource);
        }

        return success;

    }

}