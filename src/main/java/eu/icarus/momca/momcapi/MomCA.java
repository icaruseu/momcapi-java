package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.atomid.CharterAtomId;
import eu.icarus.momca.momcapi.exception.MomCAException;
import eu.icarus.momca.momcapi.existquery.ExistQuery;
import eu.icarus.momca.momcapi.existquery.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.Charter;
import eu.icarus.momca.momcapi.resource.ExistResource;
import eu.icarus.momca.momcapi.resource.User;
import nu.xom.ParsingException;
import org.jetbrains.annotations.NotNull;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by daniel on 24.06.2015.
 */
public class MomCA {

    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String FILE_ENDING_CHARTER_PUBLIC = ".cei.xml";
    private static final String FILE_ENDING_CHARTER_SAVED = ".xml";
    private static final String PATH_CHARTER_IMPORT = "/db/mom-data/metadata.charter.import";
    private static final String PATH_CHARTER_PUBLIC = "/db/mom-data/metadata.charter.public";
    private static final String PATH_CHARTER_SAVED = "/db/mom-data/metadata.charter.saved";
    private static final String PATH_FONDS_PUBLIC = "/db/mom-data/metadata.fond.public";
    private static final String PATH_USER = "/db/mom-data/xrx.user";
    private static final ExistQueryFactory QUERY_FACTORY = new ExistQueryFactory();
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

    public void closeConnection() throws MomCAException {
        try {
            rootCollection.close();
        } catch (XMLDBException e) {
            throw new MomCAException("Failed to close the database connection.", e);
        }
    }

    @NotNull
    public List<Charter> getImportedCharter(@NotNull CharterAtomId charterAtomId) throws MomCAException {

        List<Charter> charters = new ArrayList<>(0);
        for (String charterUri : queryDatabase(QUERY_FACTORY.queryUrisOfImportedCharter(charterAtomId))) {
            getCharterFromUri(charterUri).ifPresent(charters::add);
        }
        return charters;

    }

    @NotNull
    public Optional<Charter> getPublishedCharter(@NotNull CharterAtomId charterAtomId) throws MomCAException {
        String resourceName = charterAtomId.getCharterId() + FILE_ENDING_CHARTER_PUBLIC;
        String parentCollectionUri = String.join("/", PATH_CHARTER_PUBLIC, charterAtomId.getBasePath());
        return getExistResource(resourceName, parentCollectionUri).map(e -> new Charter(e));
    }

    @NotNull
    public Optional<Charter> getSavedCharter(@NotNull CharterAtomId charterAtomId) throws MomCAException {
        String resourceName = charterAtomId.getAtomId().replace("/", "#") + FILE_ENDING_CHARTER_SAVED;
        return getExistResource(resourceName, PATH_CHARTER_SAVED).map(e -> new Charter(e));
    }

    @NotNull
    public Optional<User> getUser(@NotNull String userName) throws MomCAException {
        return getExistResource(userName + ".xml", PATH_USER).flatMap(existResource -> Optional.of(new User(existResource)));
    }

    @NotNull
    public List<String> listUsers() throws MomCAException {
        return listUserResourceNames().stream().map(s -> s.replace(".xml", "")).collect(Collectors.toList());
    }

    private Optional<Charter> getCharterFromUri(String charterUri) throws MomCAException {
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
    private Optional<XMLResource> getXMLResource(@NotNull String resourceName, @NotNull Collection parentCollection) throws MomCAException {

        String encodedName;
        try {
            encodedName = URLEncoder.encode(URLDecoder.decode(resourceName, URL_ENCODING), URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new MomCAException(String.format("URL-Encoding '%s' not supported.", URL_ENCODING), e);
        }

        try {
            return Optional.ofNullable((XMLResource) parentCollection.getResource(encodedName));
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

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new MomCAException("Failed to initialize database connection.", e);
        } catch (XMLDBException e) {
            if (e.getMessage().equals("Wrong password for user [admin] ")) {
                throw new MomCAException("Wrong admin password!", e);
            } else {
                throw new MomCAException(String.format("Failed to connect to remote database '%s'", dbRootUri), e);
            }
        }


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
    private List<String> queryDatabase(ExistQuery existQuery) throws MomCAException {

        XPathQueryService queryService;
        try {
            queryService = (XPathQueryService) rootCollection.getService("XPathQueryService", "1.0");
        } catch (XMLDBException e) {
            throw new MomCAException("Failed to get the XPath query service.", e);
        }

        ResourceSet resultSet;
        try {
            resultSet = queryService.query(existQuery.getQuery());
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