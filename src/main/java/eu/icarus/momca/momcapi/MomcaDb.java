package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.id.CharterId;
import eu.icarus.momca.momcapi.resource.Charter;
import eu.icarus.momca.momcapi.resource.ExistResource;
import eu.icarus.momca.momcapi.resource.User;
import nu.xom.ParsingException;
import org.jetbrains.annotations.NotNull;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;
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
public class MomcaDb {

    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String FILE_ENDING_CHARTER_PUBLIC = ".cei.xml";
    private static final String FILE_ENDING_CHARTER_SAVED = ".xml";
    private static final String PATH_CHARTER_IMPORT = "/db/mom-data/metadata.charter.import";
    private static final String PATH_CHARTER_PUBLIC = "/db/mom-data/metadata.charter.public";
    private static final String PATH_CHARTER_SAVED = "/db/mom-data/metadata.charter.saved";
    private static final String PATH_FONDS_PUBLIC = "/db/mom-data/metadata.fond.public";
    private static final String PATH_USER = "/db/mom-data/xrx.user";
    @NotNull
    private final String admin;
    @NotNull
    private final String dbRootUri;
    @NotNull
    private final String password;
    private Collection rootCollection;


    public MomcaDb(@NotNull String dbRootUri, @NotNull String admin, @NotNull String password) throws XMLDBException, IllegalAccessException, InstantiationException, ClassNotFoundException {

        this.dbRootUri = dbRootUri;
        this.admin = admin;
        this.password = password;

        registerDatabases();

    }

    @NotNull
    public Optional<Charter> getImportedCharter(@NotNull CharterId charterId) throws XMLDBException, IOException, ParsingException {
        String resourceName = charterId.getCharterId() + FILE_ENDING_CHARTER_PUBLIC;
        String parentCollectionUri = String.join("/", PATH_CHARTER_IMPORT, charterId.getBasePath());
        return getExistResource(resourceName, parentCollectionUri).map(e -> new Charter(e, charterId));
    }

    @NotNull
    public Optional<Charter> getPublishedCharter(@NotNull CharterId charterId) throws XMLDBException, IOException, ParsingException {
        String resourceName = charterId.getCharterId() + FILE_ENDING_CHARTER_PUBLIC;
        String parentCollectionUri = String.join("/", PATH_CHARTER_PUBLIC, charterId.getBasePath());
        return getExistResource(resourceName, parentCollectionUri).map(e -> new Charter(e, charterId));
    }

    @NotNull
    public Optional<Charter> getSavedCharter(@NotNull CharterId charterId) throws XMLDBException, IOException, ParsingException {
        String resourceName = charterId.getAtomId().replace("/", "#") + FILE_ENDING_CHARTER_SAVED;
        return getExistResource(resourceName, PATH_CHARTER_SAVED).map(e -> new Charter(e, charterId));
    }

    @NotNull
    public Optional<User> getUser(@NotNull String userName) throws XMLDBException, IOException, ParsingException {
        return getExistResource(userName + ".xml", PATH_USER).flatMap(existResource -> Optional.of(new User(existResource)));
    }

    @NotNull
    public List<String> listUserResourceNames() throws XMLDBException, UnsupportedEncodingException {

        Optional<Collection> userCollection = getCollection(PATH_USER);
        List<String> users = new ArrayList<>();
        if (userCollection.isPresent()) {
            String[] escapedUserNames = userCollection.get().listResources();
            for (String escapedUserName : escapedUserNames) {
                users.add(URLDecoder.decode(escapedUserName, "UTF-8"));
            }
        }
        users.sort(Comparator.<String>naturalOrder());
        return users;

    }

    @NotNull
    public List<String> listUsers() throws XMLDBException, UnsupportedEncodingException {
        return listUserResourceNames().stream().map(s -> s.replace(".xml", "")).collect(Collectors.toList());
    }

    @NotNull
    public List<String> queryDatabase(String query) throws XMLDBException {

        XPathQueryService queryService = (XPathQueryService) rootCollection.getService("XPathQueryService", "1.0");
        ResourceSet resultSet = queryService.query(query);
        ResourceIterator iterator = resultSet.getIterator();

        List<String> resultList = new ArrayList<>(0);
        while (iterator.hasMoreResources()) {
            XMLResource resource = (XMLResource) iterator.nextResource();
            resultList.add(resource.getContent().toString());
        }

        return resultList;

    }

    @NotNull
    private Optional<Collection> getCollection(@NotNull String uri) throws XMLDBException {
        return Optional.ofNullable(DatabaseManager.getCollection(dbRootUri + uri, admin, password));
    }

    @NotNull
    private Optional<ExistResource> getExistResource(@NotNull String resourceName, @NotNull String parentCollectionPath) throws XMLDBException, IOException, ParsingException {

        Optional<ExistResource> existResource = Optional.empty();

        Optional<Collection> collection = getCollection(parentCollectionPath);
        if (collection.isPresent()) {
            Optional<XMLResource> resource = getXMLResource(resourceName, collection.get());
            if (resource.isPresent()) {
                existResource = Optional.of(new ExistResource(resourceName, parentCollectionPath, getResourceContent(resource.get())));
            }
        }

        return existResource;

    }

    @NotNull
    private String getResourceContent(@NotNull XMLResource xmlResource) throws XMLDBException {
        return (String) xmlResource.getContent();
    }

    @NotNull
    private Optional<XMLResource> getXMLResource(@NotNull String resourceName, @NotNull Collection parentCollection) throws UnsupportedEncodingException, XMLDBException {
        String encodedName = URLEncoder.encode(resourceName, "UTF-8");
        return Optional.ofNullable((XMLResource) parentCollection.getResource(encodedName));
    }

    /**
     * Register the database
     */
    private void registerDatabases() throws ClassNotFoundException, IllegalAccessException, InstantiationException, XMLDBException {

        Class cl = Class.forName(DRIVER);
        Database dbDatabase = (Database) cl.newInstance();
        DatabaseManager.registerDatabase(dbDatabase);

        Optional<Collection> root = getCollection("/db");
        root.ifPresent(collection -> this.rootCollection = collection);

    }

}