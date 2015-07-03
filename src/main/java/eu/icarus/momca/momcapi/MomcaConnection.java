package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomCAException;
import eu.icarus.momca.momcapi.exist.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.ExistResource;
import nu.xom.ParsingException;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by daniel on 24.06.2015.
 */
public class MomcaConnection {

    @NotNull
    static final String URL_ENCODING = "UTF-8";
    @NotNull
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    @NotNull
    private static final ExistQueryFactory QUERY_FACTORY = new ExistQueryFactory();
    @NotNull
    private static final String ROOT_COLLECTION = "/db/mom-data";
    @NotNull
    private final String admin;
    @NotNull
    private final String dbRootUri;
    @NotNull
    private final String password;
    private Collection rootCollection;

    public MomcaConnection(@NotNull String dbRootUri, @NotNull String admin, @NotNull String password) throws MomCAException {

        this.dbRootUri = dbRootUri;
        this.admin = admin;
        this.password = password;

        initDatabaseConnection();

    }

    public void closeConnection() throws MomCAException {

        try {
            rootCollection.close();
        } catch (XMLDBException e) {
            throw new MomCAException("Failed to close the database connection.", e);
        }

    }

    @NotNull
    public CharterManager getCharterManager() {
        return new CharterManager(this);
    }

    @NotNull
    public UserManager getUserManager() {
        return new UserManager(this);
    }

    void deleteCollection(@NotNull String uri) throws MomCAException {

        Optional<Collection> collectionOptional = getCollection(uri);

        if (collectionOptional.isPresent()) {
            Collection collection = collectionOptional.get();
            try {
                RemoteCollectionManagementService service = (RemoteCollectionManagementService) collection.getParentCollection().getService("CollectionManagementService", "1.0");
                service.removeCollection(((RemoteCollection) collection).getPathURI());
            } catch (XMLDBException e) {
                e.printStackTrace();
            }
        }

    }

    void deleteExistResource(@NotNull ExistResource resourceToDelete) throws MomCAException {

        Optional<Collection> parent = getCollection(resourceToDelete.getParentUri());

        if (parent.isPresent()) {

            try {
                Resource res = parent.get().getResource(findMatchingResource(resourceToDelete.getResourceName(), parent.get().listResources()));
                if (res != null) {
                    parent.get().removeResource(res);
                }

            } catch (XMLDBException e) {
                throw new MomCAException("Failed to remove the resource '" + resourceToDelete.getUri() + "'", e);
            }

        }

    }

    @NotNull
    Optional<Collection> getCollection(@NotNull String uri) throws MomCAException {

        try {
            return Optional.ofNullable(DatabaseManager.getCollection(dbRootUri + Util.encode(uri), admin, password));
        } catch (@NotNull UnsupportedEncodingException | XMLDBException e) {
            throw new MomCAException(String.format("Failed to open collection '%s'.", uri), e);
        }

    }

    @NotNull
    Optional<ExistResource> getExistResource(@NotNull String resourceName, @NotNull String parentCollectionPath) throws MomCAException {

        Optional<ExistResource> existResource = Optional.empty();
        Optional<Collection> collection = getCollection(parentCollectionPath);

        if (collection.isPresent()) {

            Optional<XMLResource> resource = getXMLResource(resourceName, collection.get());
            if (resource.isPresent()) {

                String content;
                try {
                    content = (String) resource.get().getContent();
                } catch (XMLDBException e) {
                    throw new MomCAException(String.format("Failed to get content of resource '%s'.", resourceName), e);
                }

                try {
                    existResource = Optional.of(new ExistResource(resourceName, parentCollectionPath, content));
                } catch (ParsingException e) {
                    throw new MomCAException(String.format("Content of '%s' is not well-formed XML.", resourceName));
                } catch (IOException e) {
                    throw new MomCAException("Failed to read the documents external DTD subset due to an I/O error.", e);
                }

            }
        }

        return existResource;

    }

    Collection getRootCollection() {
        return rootCollection;
    }

    @NotNull
    List<String> queryDatabase(@NotNull String existQuery) throws MomCAException {

        XPathQueryService queryService;
        try {
            queryService = (XPathQueryService) rootCollection.getService("XPathQueryService", "1.0");
        } catch (XMLDBException e) {
            throw new MomCAException("Failed to get the XPath query service.", e);
        }

        ResourceSet resultSet;
        try {
            resultSet = queryService.query(existQuery);
        } catch (XMLDBException e) {
            throw new MomCAException(String.format("Failed to execute query '%s'", existQuery), e);
        }

        List<String> resultList = new ArrayList<>(0);
        try {
            ResourceIterator iterator = resultSet.getIterator();
            while (iterator.hasMoreResources()) {
                XMLResource resource = (XMLResource) iterator.nextResource();
                resultList.add(resource.getContent().toString());
            }
        } catch (XMLDBException e) {
            throw new MomCAException("Failed to extract results from query resultSet.", e);
        }

        return resultList;

    }

    /**
     * @param resource the resource to write in the database. If a resource with the same uri is already existing, it gets overwritten.
     * @throws MomCAException on problems to create the resource
     */
    void storeExistResource(@NotNull ExistResource resource) throws MomCAException {

        Optional<Collection> parent = getCollection(resource.getParentUri());

        if (parent.isPresent()) {

            Collection col = parent.get();

            try {

                XMLResource newResource = (XMLResource) col.createResource(resource.getResourceName(), "XMLResource");
                newResource.setContent(resource.getXmlAsDocument().toXML());
                col.storeResource(newResource);

                UserManagementService userService = (RemoteUserManagementService) col.getService("UserManagementService", "1.0");
                userService.chmod(newResource, "rwxrwxrwx");

                col.close();

            } catch (XMLDBException e) {
                throw new MomCAException("Failed to create new resource.", e);
            }

        }

    }

    private void addCollection(@NotNull String name, @NotNull String parentUri) throws MomCAException {

        String encodedName;
        String encodedPath;

        try {
            encodedPath = Util.encode(parentUri);
            encodedName = Util.encode(name);

        } catch (UnsupportedEncodingException e) {
            throw new MomCAException("Failed to encode uri.", e);
        }

        Optional<Collection> parentOptional = getCollection(encodedPath);

        if (parentOptional.isPresent()) {

            Collection parent = parentOptional.get();
            try {
                CollectionManagementService parentService = (RemoteCollectionManagementService) parent.getService("CollectionManagementService", "1.0");
                parentService.createCollection(encodedName);

                Collection newCollection = parent.getChildCollection(encodedName);
                UserManagementService userService = (RemoteUserManagementService) newCollection.getService("UserManagementService", "1.0");
                userService.chmod("rwxrwxrwx");

            } catch (XMLDBException e) {
                throw new MomCAException("Failed to add collection.", e);
            }

        }

    }

    /**
     * @param resourceName the name of the resource to find in the list
     * @param resources    the list of resources
     * @return The actual name of the resource in the list of resources without regardless of eventual URL-Encodings for special characters like @ and |
     * @throws MomCAException if character encoding needs to be consulted, but named character encoding is not supported
     */
    @NotNull
    private String findMatchingResource(@NotNull String resourceName, @NotNull String[] resources) throws MomCAException {

        String matchingName = "";
        for (String resource : resources) {

            try {
                if (URLDecoder.decode(resource, URL_ENCODING).equals(URLDecoder.decode(resourceName, URL_ENCODING))) {
                    matchingName = resource;
                }
            } catch (UnsupportedEncodingException e) {
                throw new MomCAException(String.format("URL-Encoding '%s' not supported.", URL_ENCODING), e);
            }

        }
        return matchingName;

    }

    @NotNull
    private Optional<XMLResource> getXMLResource(@NotNull String resourceName, @NotNull Collection parentCollection) throws MomCAException {

        try {
            return Optional.ofNullable((XMLResource) parentCollection.getResource(findMatchingResource(resourceName, parentCollection.listResources())));
        } catch (XMLDBException e) {
            throw new MomCAException(String.format("Failed to get resource '%s' from parent collection.", resourceName), e);
        }

    }

    /**
     * Register the database
     */
    private void initDatabaseConnection() throws MomCAException {

        try {

            org.xmldb.api.base.Database dbDatabase = (org.xmldb.api.base.Database) Class.forName(DRIVER).newInstance();
            DatabaseManager.registerDatabase(dbDatabase);

            Optional<Collection> root = getCollection("/db");
            root.ifPresent(collection -> this.rootCollection = collection);

        } catch (@NotNull ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new MomCAException("Failed to initialize database connection.", e);
        } catch (XMLDBException e) {
            if (e.getMessage().equals("Wrong password for user [admin] ")) {
                throw new MomCAException("Wrong admin password!", e);
            } else {
                throw new MomCAException(String.format("Failed to connect to remote database '%s'", dbRootUri), e);
            }
        }


    }

}