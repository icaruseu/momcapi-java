package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.atomid.CharterAtomId;
import eu.icarus.momca.momcapi.exception.MomCAException;
import eu.icarus.momca.momcapi.exist.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.Charter;
import eu.icarus.momca.momcapi.resource.CharterStatus;
import eu.icarus.momca.momcapi.resource.ExistResource;
import eu.icarus.momca.momcapi.resource.User;
import nu.xom.ParsingException;
import org.exist.security.Account;
import org.exist.security.Group;
import org.exist.security.internal.aider.GroupAider;
import org.exist.security.internal.aider.UserAider;
import org.exist.xmldb.RemoteCollectionManagementService;
import org.exist.xmldb.RemoteUserManagementService;
import org.exist.xmldb.XmldbURI;
import org.jetbrains.annotations.NotNull;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by daniel on 24.06.2015.
 */
public class MomCA {

    @NotNull
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    @NotNull
    private static final String NEW_USER_CONTENT = "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"> <xrx:username /> <xrx:password /> <xrx:firstname>%s</xrx:firstname> <xrx:name>%s</xrx:name> <xrx:email>%s</xrx:email> <xrx:moderator>%s</xrx:moderator> <xrx:street /> <xrx:zip /> <xrx:town /> <xrx:phone /> <xrx:institution /> <xrx:info /> <xrx:storage> <xrx:saved_list /> <xrx:bookmark_list /> </xrx:storage> </xrx:user>";
    @NotNull
    private static final String PATH_USER = "/db/mom-data/xrx.user";
    @NotNull
    private static final ExistQueryFactory QUERY_FACTORY = new ExistQueryFactory();
    @NotNull
    private static final String ROOT_COLLECTION = "/db/mom-data";
    @NotNull
    private static final String URL_ENCODING = "UTF-8";
    @NotNull
    private final String admin;
    @NotNull
    private final String dbRootUri;
    @NotNull
    private final String password;
    private Collection rootCollection;


    public MomCA(@NotNull String dbRootUri, @NotNull String admin, @NotNull String password) throws MomCAException {

        this.dbRootUri = dbRootUri;
        this.admin = admin;
        this.password = password;

        initDatabaseConnection();

    }

    public void addExistUserAccount(String userName, String password) throws MomCAException {

        String atom = "atom";

        try {

            RemoteUserManagementService service = (RemoteUserManagementService) rootCollection.getService("UserManagementService", "1.0");

            Group atomGroup = new GroupAider(atom);
            service.addGroup(atomGroup);

            Account newAccount = new UserAider(userName, atomGroup);
            newAccount.setPassword(password);
            service.addAccount(newAccount);
            service.addAccountToGroup(userName, "guest");

        } catch (XMLDBException e) {
            if (!e.getMessage().equals(String.format("Failed to invoke method addAccount in class org.exist.xmlrpc.RpcConnection: Account '%s' exist", userName))) {
                throw new MomCAException("Failed to create user '" + userName + "'", e);
            }
        }

    }

    public void addUser(@NotNull String userName, @NotNull String password, @NotNull String moderatorName) throws MomCAException {

        if (!getUser(userName).isPresent()) {

            String xmlContent = String.format(NEW_USER_CONTENT, "New", "User", userName, moderatorName);
            ExistResource userResource;
            try {
                userResource = new ExistResource(userName + ".xml", PATH_USER, xmlContent);
            } catch (ParsingException | IOException e) {
                throw new MomCAException("Failed to create new user resource.", e);
            }
            storeExistResource(userResource);

            addExistUserAccount(userName, password);

        }

    }

    public void changeUserPassword(@NotNull String userName, @NotNull String newPassword) throws MomCAException {

        try {

            RemoteUserManagementService service = (RemoteUserManagementService) rootCollection.getService("UserManagementService", "1.0");
            Account account = service.getAccount(userName);
            if (account != null) {
                account.setPassword(newPassword);
                service.updateAccount(account);
            }

        } catch (XMLDBException e) {
            throw new MomCAException("Failed to change the password for '" + userName + "'", e);
        }

    }

    public void closeConnection() throws MomCAException {

        try {
            rootCollection.close();
        } catch (XMLDBException e) {
            throw new MomCAException("Failed to close the database connection.", e);
        }

    }

    public void deleteExistResource(ExistResource resourceToDelete) throws MomCAException {

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

    public void deleteExistUserAccount(@NotNull String userName) throws MomCAException {

        try {

            RemoteUserManagementService service = (RemoteUserManagementService) rootCollection.getService("UserManagementService", "1.0");
            Account account = service.getAccount(userName);

            if (account != null) {
                service.removeAccount(account);
            }

        } catch (XMLDBException e) {
            throw new MomCAException("Failed to remove account '" + userName + "'", e);
        }

    }

    public void deleteUser(@NotNull String userName) throws MomCAException {

        Optional<User> userOptional = getUser(userName);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            deleteExistResource(user);
        }

        deleteExistUserAccount(userName);

        deleteCollection(PATH_USER + "/" + userName);

    }

    @NotNull
    public List<Charter> getImportedCharters(@NotNull CharterAtomId charterAtomId) throws MomCAException {
        return getMatchingCharters(charterAtomId, CharterStatus.IMPORTED.getParentCollection());
    }

    @NotNull
    public List<Charter> getPrivateCharters(@NotNull CharterAtomId charterAtomId, @NotNull String userName) throws MomCAException {
        return getMatchingCharters(charterAtomId, CharterStatus.PRIVATE.getParentCollection() + "/" + userName + "/metadata.charter");
    }

    @NotNull
    public List<Charter> getPublishedCharters(@NotNull CharterAtomId charterAtomId) throws MomCAException {
        return getMatchingCharters(charterAtomId, CharterStatus.PUBLIC.getParentCollection());
    }

    @NotNull
    public List<Charter> getSavedCharters(@NotNull CharterAtomId charterAtomId) throws MomCAException {
        return getMatchingCharters(charterAtomId, CharterStatus.SAVED.getParentCollection());
    }

    @NotNull
    public Optional<User> getUser(@NotNull String userName) throws MomCAException {
        return getExistResource(userName + ".xml", PATH_USER).flatMap(existResource -> Optional.of(new User(existResource)));
    }

    public boolean isUserInitialized(@NotNull String userName) throws MomCAException {

        try {
            RemoteUserManagementService service = (RemoteUserManagementService) rootCollection.getService("UserManagementService", "1.0");
            return Optional.ofNullable(service.getAccount(userName)).isPresent();
        } catch (XMLDBException e) {
            throw new MomCAException("Failed to get resource for user '" + userName + "'", e);
        }

    }

    @NotNull
    public List<CharterAtomId> listErroneouslySavedCharters(@NotNull String userName) throws MomCAException {

        List<CharterAtomId> erroneouslySavedCharters = new ArrayList<>(0);
        getUser(userName).ifPresent(user -> erroneouslySavedCharters.addAll(user.listSavedCharterIds()));

        for (CharterAtomId id : erroneouslySavedCharters) {
            if (isCharterExisting(id, CharterStatus.SAVED)) {
                erroneouslySavedCharters.remove(id);
            }
        }

        return erroneouslySavedCharters;

    }

    @NotNull
    public List<String> listUsers() throws MomCAException {
        return listUserResourceNames().stream().map(s -> s.replace(".xml", "")).collect(Collectors.toList());
    }

    /**
     * @param resource the resource to write in the database. If a resource with the same uri is already existing, it gets overwritten.
     * @throws MomCAException on problems to create the resource
     */
    public void storeExistResource(@NotNull ExistResource resource) throws MomCAException {

        Optional<Collection> parent = getCollection(resource.getParentUri());

        if (parent.isPresent()) {

            Collection col = parent.get();

            try {

                XMLResource newResource = (XMLResource) col.createResource(resource.getResourceName(), "XMLResource");
                newResource.setContent(resource.getXmlAsDocument().toXML());
                col.storeResource(newResource);
                col.close();

            } catch (XMLDBException e) {
                throw new MomCAException("Failed to create new resource.", e);
            }

        }

    }

    private void deleteCollection(@NotNull String uri) throws MomCAException {

        Optional<Collection> collectionOptional = getCollection(uri);

        if (collectionOptional.isPresent()) {
            Collection collection = collectionOptional.get();
            try {
                RemoteCollectionManagementService service = (RemoteCollectionManagementService) collection.getParentCollection().getService("CollectionManagementService", "1.0");
                service.removeCollection(XmldbURI.create(uri));
            } catch (XMLDBException e) {
                e.printStackTrace();
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
    private Optional<Charter> getCharterFromUri(@NotNull String charterUri) throws MomCAException {
        String resourceName = charterUri.substring(charterUri.lastIndexOf('/') + 1, charterUri.length());
        String parentUri = charterUri.substring(0, charterUri.lastIndexOf('/'));
        return getExistResource(resourceName, parentUri).map(Charter::new);
    }

    @NotNull
    private Optional<Collection> getCollection(@NotNull String uri) throws MomCAException {

        try {
            return Optional.ofNullable(DatabaseManager.getCollection(dbRootUri + uri, admin, password));
        } catch (XMLDBException e) {
            throw new MomCAException(String.format("Failed to open collection '%s'.", uri), e);
        }

    }

    @NotNull
    private Optional<ExistResource> getExistResource(@NotNull String resourceName, @NotNull String parentCollectionPath) throws MomCAException {

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

    @NotNull
    private List<Charter> getMatchingCharters(@NotNull CharterAtomId charterAtomId, @NotNull String parentCollection) throws MomCAException {

        String path;
        if (parentCollection.equals(CharterStatus.SAVED.getParentCollection())) {

            String[] parts = {ROOT_COLLECTION, parentCollection};
            path = String.join("/", parts);

        } else if (charterAtomId.isPartOfArchiveFond()) {

            String[] parts = {ROOT_COLLECTION, parentCollection, charterAtomId.getArchiveId().get(), charterAtomId.getFondId().get()};
            path = String.join("/", parts);

        } else {

            String[] parts = {ROOT_COLLECTION, parentCollection, charterAtomId.getCollectionId().get()};
            path = String.join("/", parts);

        }

        List<Charter> charters = new ArrayList<>(0);
        for (String charterUri : queryDatabase(QUERY_FACTORY.queryUrisOfCharter(path, charterAtomId.getCharterId()))) {
            getCharterFromUri(charterUri).ifPresent(charters::add);
        }
        return charters;

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

    private boolean isCharterExisting(@NotNull CharterAtomId charterAtomId, @NotNull CharterStatus status) throws MomCAException {
        return !getMatchingCharters(charterAtomId, status.getParentCollection()).isEmpty();
    }

    @NotNull
    private List<String> listUserResourceNames() throws MomCAException {

        Optional<Collection> userCollection = getCollection(PATH_USER);

        List<String> users = new ArrayList<>();
        if (userCollection.isPresent()) {

            String[] escapedUserNames;
            try {
                escapedUserNames = userCollection.get().listResources();
            } catch (XMLDBException e) {
                throw new MomCAException(String.format("Failed to list resources in collection '%s'.", PATH_USER), e);
            }

            for (String escapedUserName : escapedUserNames) {
                try {
                    users.add(URLDecoder.decode(escapedUserName, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new MomCAException(String.format("URL-Encoding '%s' not supported.", URL_ENCODING), e);
                }
            }

        }

        users.sort(Comparator.<String>naturalOrder());
        return users;

    }

    @NotNull
    private List<String> queryDatabase(@NotNull String existQuery) throws MomCAException {

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

}