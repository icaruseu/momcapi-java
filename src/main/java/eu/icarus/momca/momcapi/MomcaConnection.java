package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.query.ExistQuery;
import eu.icarus.momca.momcapi.resource.MomcaResource;
import org.exist.xmldb.RemoteCollection;
import org.exist.xmldb.RemoteCollectionManagementService;
import org.exist.xmldb.RemoteUserManagementService;
import org.exist.xmldb.UserManagementService;
import org.jetbrains.annotations.NotNull;
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
    @NotNull
    private final String admin;
    @NotNull
    private final CharterManager charterManager;
    @NotNull
    private final CountryManager countryManager;
    @NotNull
    private final String dbRootUri;
    @NotNull
    private final FondManager fondManager;
    @NotNull
    private final ArchiveManager archiveManager;
    @NotNull
    private final String password;
    @NotNull
    private final Collection rootCollection;
    @NotNull
    private final UserManager userManager;

    /**
     * Instantiates a new MomcaConnection.
     *
     * @param dbRootUri The root URI of the database, e.g. {@code xmldb:exist://localhost:8181/xmlrpc}.
     * @param admin     The name of the admin user, e.g. {@code admin}.
     * @param password  The password of the admin user.
     */
    public MomcaConnection(@NotNull String dbRootUri, @NotNull String admin, @NotNull String password) {

        this.dbRootUri = dbRootUri;
        this.admin = admin;
        this.password = password;

        rootCollection = initDatabaseConnection();

        userManager = new UserManager(this);
        countryManager = new CountryManager(this);
        archiveManager = new ArchiveManager(this);
        fondManager = new FondManager(this);
        charterManager = new CharterManager(this);

    }

    /**
     * Close the Connection.
     */
    public void closeConnection() {

        try {
            rootCollection.close();
        } catch (XMLDBException e) {
            throw new MomcaException("Failed to close the database connection.", e);
        }

    }

    /**
     * @return The charter manager instance.
     */
    @NotNull
    public CharterManager getCharterManager() {
        return charterManager;
    }

    /**
     * @return The country manager instance.
     */
    @NotNull
    public CountryManager getCountryManager() {
        return countryManager;
    }

    @NotNull
    public FondManager getFondManager() {
        return fondManager;
    }

    /**
     * @return The hierarchy manager instance.
     */
    @NotNull
    public ArchiveManager getArchiveManager() {
        return archiveManager;
    }

    /**
     * @return The user manager instance.
     */
    @NotNull
    public UserManager getUserManager() {
        return userManager;
    }

    /**
     * Adds an empty eXist collection to the database.
     *
     * @param name      The name of the collection.
     * @param parentUri The absolute URI of the collections parent collection, e.g. {@code /db/mom-data/metadata.fond.public}.
     * @see Collection
     */
    void addCollection(@NotNull String name, @NotNull String parentUri) {
        String encodedName = Util.encode(name);
        String encodedPath = Util.encode(parentUri);
        getCollection(encodedPath).ifPresent(parent -> createCollectionInExist(name, parentUri, encodedName, parent));
    }

    /**
     * Deletes an collection from the database.
     *
     * @param uri The absolute URI of the collection to delete, e.g. {@code /db/mom-data/metadata.fond.pubic/collectionToDelete}.
     * @see Collection
     */
    void deleteCollection(@NotNull String uri) {
        getCollection(uri).ifPresent(collection -> removeCollectionInExist(uri, collection));
    }

    /**
     * Deletes an resource from the database.
     *
     * @param resourceToDelete The resource to delete.
     */
    void deleteExistResource(@NotNull MomcaResource resourceToDelete) {
        getCollection(resourceToDelete.getParentUri()).ifPresent(parent -> removeResourceInExist(resourceToDelete, parent));
    }

    /**
     * Tries to get a collection from the database.
     *
     * @param uri The absolute URI of the collection to get, e.g. {@code /db/mom-data/metadata.fond.pubic/collectionToGet}.
     * @return The optional collection.
     * @see Collection
     */
    @NotNull
    Optional<Collection> getCollection(@NotNull String uri) {

        try {
            return Optional.ofNullable(DatabaseManager.getCollection(dbRootUri + Util.encode(uri), admin, password));
        } catch (@NotNull XMLDBException e) {
            throw new MomcaException(String.format("Failed to get collection '%s'.", uri), e);
        }

    }

    /**
     * Tries to get a resource from the database.
     *
     * @param resourceName         The name of the resource, e.g. {@code admin.xml}.
     * @param parentCollectionPath The absolute URI of the parent collection in the database, e.g. {@code /db/mom-data/xrx.user}.
     * @return The MomcaResource.
     */
    @NotNull
    Optional<MomcaResource> getExistResource(@NotNull String resourceName, @NotNull String parentCollectionPath) {
        return getCollection(parentCollectionPath)
                .flatMap(collection -> getXMLResource(resourceName, collection)
                        .flatMap(resource -> createExistResourceFromXMLResource(resourceName, parentCollectionPath, resource)));
    }

    /**
     * Gets root collection of the database, {@code /db}.
     *
     * @return The root collection.
     */
    @NotNull
    Collection getRootCollection() {
        return rootCollection;
    }

    /**
     * Queries the database.
     *
     * @param existQuery The query to execute.
     * @return A list with the resulting XML fragments as {@code String}.
     */
    @NotNull
    List<String> queryDatabase(@NotNull ExistQuery existQuery) {

        XPathQueryService queryService;
        try {
            queryService = (XPathQueryService) rootCollection.getService("XPathQueryService", "1.0");
        } catch (XMLDBException e) {
            throw new MomcaException("Failed to get the XPath query service.", e);
        }

        ResourceSet resultSet;
        try {
            resultSet = queryService.query(existQuery.getQuery());
        } catch (XMLDBException e) {
            throw new MomcaException(String.format("Failed to execute query '%s'", existQuery.getQuery()), e);
        }

        List<String> resultList = new ArrayList<>(0);
        try {
            ResourceIterator iterator = resultSet.getIterator();
            while (iterator.hasMoreResources()) {
                XMLResource resource = (XMLResource) iterator.nextResource();
                resultList.add(resource.getContent().toString());
            }
        } catch (XMLDBException e) {
            throw new MomcaException("Failed to extract results from query resultSet.", e);
        }

        return resultList;

    }

    /**
     * Stores a MomcaResource in the database.
     *
     * @param resource The resource to store in the database. If a resource with the same uri is already existing, it gets overwritten.
     */
    void storeExistResource(@NotNull MomcaResource resource) {
        getCollection(resource.getParentUri()).ifPresent(collection -> storeResourceInExist(resource, collection));
    }

    private void createCollectionInExist(@NotNull String name, @NotNull String parentUri, @NotNull String encodedName, @NotNull Collection parent) {

        try {

            CollectionManagementService parentService = (RemoteCollectionManagementService) parent.getService("CollectionManagementService", "1.0");
            parentService.createCollection(encodedName);

            Collection newCollection = parent.getChildCollection(encodedName);
            UserManagementService userService = (RemoteUserManagementService) newCollection.getService("UserManagementService", "1.0");
            userService.chmod("rwxrwxrwx");

        } catch (XMLDBException e) {
            throw new MomcaException(String.format("Failed to add collection '%s/%s'.", parentUri, name), e);
        }

    }

    @NotNull
    private Optional<MomcaResource> createExistResourceFromXMLResource(@NotNull String resourceName, @NotNull String parentCollectionPath, @NotNull XMLResource resource) {

        String content;
        try {
            content = (String) resource.getContent();
        } catch (XMLDBException e) {
            throw new MomcaException(String.format("Failed to get content of resource '%s'.", resourceName), e);
        }
        return Optional.of(new MomcaResource(resourceName, parentCollectionPath, content));

    }

    /**
     * @param resourceName the name of the resource to find in the list
     * @param resources    the list of resources
     * @return The actual name of the resource in the list of resources without regardless of eventual URL-Encodings for special characters like @ and |
     */
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
    private Optional<XMLResource> getXMLResource(@NotNull String resourceName, @NotNull Collection collection) {

        try {
            return Optional.ofNullable((XMLResource) collection.getResource(findMatchingResource(resourceName, collection.listResources())));
        } catch (XMLDBException e) {
            throw new MomcaException(String.format("Failed to get resource '%s' from parent collection.", resourceName), e);
        }

    }

    /**
     * Register the database
     */
    private Collection initDatabaseConnection() {

        try {

            org.xmldb.api.base.Database dbDatabase = (org.xmldb.api.base.Database) Class.forName(DRIVER).newInstance();
            DatabaseManager.registerDatabase(dbDatabase);
            return getCollection("/db").get();

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

    private void removeCollectionInExist(@NotNull String uri, @NotNull Collection collection) {

        try {
            RemoteCollectionManagementService service = (RemoteCollectionManagementService) collection.getParentCollection().getService("CollectionManagementService", "1.0");
            service.removeCollection(((RemoteCollection) collection).getPathURI());
        } catch (XMLDBException e) {
            throw new MomcaException(String.format("Failed to delete collection '%s'", uri), e);
        }

    }

    private void removeResourceInExist(@NotNull MomcaResource resourceToDelete, @NotNull Collection collection) {

        try {
            Resource res = collection.getResource(findMatchingResource(resourceToDelete.getResourceName(), collection.listResources()));
            if (res != null) {
                collection.removeResource(res);
            }
        } catch (@NotNull XMLDBException e) {
            throw new MomcaException("Failed to remove the resource '" + resourceToDelete.getUri() + "'", e);
        }

    }

    private void storeResourceInExist(@NotNull MomcaResource resource, @NotNull Collection collection) {

        try {

            XMLResource newResource = (XMLResource) collection.createResource(resource.getResourceName(), "XMLResource");
            newResource.setContent(resource.getXmlAsDocument().toXML());
            collection.storeResource(newResource);

            UserManagementService userService = (RemoteUserManagementService) collection.getService("UserManagementService", "1.0");
            userService.chmod(newResource, "rwxrwxrwx");

            collection.close();

        } catch (XMLDBException e) {
            throw new MomcaException("Failed to create new resource.", e);
        }

    }

}