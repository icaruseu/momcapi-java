package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.atomid.CharterAtomId;
import eu.icarus.momca.momcapi.exist.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.Charter;
import eu.icarus.momca.momcapi.resource.ExistResource;
import eu.icarus.momca.momcapi.resource.XpathQuery;
import org.exist.xmldb.RemoteCollectionManagementService;
import org.exist.xmldb.XmldbURI;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.xmldb.api.base.Collection;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.testng.Assert.*;

/**
 * Created by daniel on 25.06.2015.
 */
public class MomCATest {

    @NotNull
    private static final ExistQueryFactory QUERY_FACTORY = new ExistQueryFactory();
    @NotNull
    private static final String SERVER_PROPERTIES_PATH = "/server.properties";
    @NotNull
    private static final String adminUser = "admin";
    @NotNull
    private static final String password = "momcapitest";
    private MomCA db;

    @BeforeClass
    public void setUp() throws Exception {

        URL serverPropertiesUrl = getClass().getResource(SERVER_PROPERTIES_PATH);
        assertNotNull(getClass().getResource(SERVER_PROPERTIES_PATH), "Test file missing");

        Properties serverProperties = new Properties();
        try (FileInputStream file = new FileInputStream(new File(serverPropertiesUrl.getPath()))) {

            BufferedInputStream stream = new BufferedInputStream(file);
            serverProperties.load(stream);
            stream.close();

        } catch (@NotNull NullPointerException | IOException e) {
            throw new RuntimeException("Failed to load properties file.", e);
        }

        String serverUrl = serverProperties.getProperty("serverUrl");

        assertNotNull(serverUrl, "'serverUrl' missing from '" + SERVER_PROPERTIES_PATH + "'");
        assertNotNull(password, "'password' missing from '" + SERVER_PROPERTIES_PATH + "'");

        db = new MomCA(serverUrl, adminUser, password);

        assertNotNull(db, "MomCA connection not initialized.");

    }

    @AfterClass
    public void tearDown() throws Exception {
        db.closeConnection();
    }

    @Test
    public void testAddCollection() throws Exception {

        String name = "te@m";
        String path = "/db";

        Class<?> cl = db.getClass();

        Method addCollection = cl.getDeclaredMethod("addCollection", String.class, String.class);
        addCollection.setAccessible(true);
        addCollection.invoke(db, name, path);

        Method removeCollection = cl.getDeclaredMethod("deleteCollection", String.class);
        removeCollection.setAccessible(true);
        removeCollection.invoke(db, path + "/" + name);

        Method getCollection = cl.getDeclaredMethod("getCollection", String.class);
        getCollection.setAccessible(true);
        //noinspection unchecked
        assertFalse(((Optional<Collection>) getCollection.invoke(db, path + "/" + name)).isPresent());

    }

    @Test
    public void testAddUser() throws Exception {

        String userName = "newlyAddedUser@dev.monasterium.net";
        String password = "newPassword";
        String moderator = "admin";
        db.addUser(userName, password, moderator);

        assertTrue(db.getUser(userName).isPresent());
        assertTrue(db.isUserInitialized(userName));

        db.deleteUser(userName);

    }

    @Test
    public void testDeleteCollection() throws Exception {

        XmldbURI uri = XmldbURI.create("/db/t|est");

        Field f = db.getClass().getDeclaredField("rootCollection");
        f.setAccessible(true);
        Collection rootCollection = (Collection) f.get(db);

        RemoteCollectionManagementService service = (RemoteCollectionManagementService) rootCollection.getService("CollectionManagementService", "1.0");
        service.createCollection(uri);

        Class<?> cl = db.getClass();
        Method deleteCollectionMethod = cl.getDeclaredMethod("deleteCollection", String.class);
        deleteCollectionMethod.setAccessible(true);
        deleteCollectionMethod.invoke(db, uri.toASCIIString());

        Method getCollectionMethod = cl.getDeclaredMethod("getCollection", String.class);
        getCollectionMethod.setAccessible(true);

        //noinspection unchecked
        assertFalse(((Optional<Collection>) getCollectionMethod.invoke(db, uri.toASCIIString())).isPresent());

    }

    @Test
    public void testDeleteExistResource() throws Exception {

        ExistResource res = new ExistResource("deleteTest.xml", "/db", "<empty/>");
        db.storeExistResource(res);
        db.deleteExistResource(res);
        assertFalse(callGetExistResourceMethod(res.getResourceName(), res.getParentUri()).isPresent());

    }

//    @Test
//    public void testChangeUserPassword() throws Exception {
//
//        String userName = "changePasswordTestUser";
//        String oldPassword = "oldPassword";
//        String newPassword = "newPassword";
//
//        Field f = MomCA.class.getDeclaredField("rootCollection");
//        f.setAccessible(true);//Abracadabra
//        Collection rootCollection = (Collection) f.get(db);
//        System.out.println(Arrays.asList(rootCollection.getServices()));
//        RemoteUserManagementService service = (RemoteUserManagementService) rootCollection.getService("UserManagementService", "1.0");
//
//        db.initializeUser(userName, oldPassword);
//        db.changeUserPassword(userName, newPassword);
//
//    }

    @Test
    public void testDeleteExistUserAccount() throws Exception {

        String userName = "removeAccountTest@dev.monasterium.net";
        String password = "testing123";
        db.initializeUser(userName, password);

        db.deleteExistUserAccount(userName);

        assertFalse(db.isUserInitialized(userName));

    }

    @Test
    public void testDeleteUser() throws Exception {

        String userName = "removeUserTest@dev.monasterium.net";
        String password = "testing123";
        String moderator = "admin";

        db.addUser(userName, password, moderator);
        db.deleteUser(userName);

        assertFalse(db.getUser(userName).isPresent());
        assertFalse(db.isUserInitialized(userName));

        Class<?> cl = db.getClass();
        Method getCollectionMethod = cl.getDeclaredMethod("getCollection", String.class);
        getCollectionMethod.setAccessible(true);
        //noinspection unchecked
        assertFalse(((Optional<Collection>) getCollectionMethod.invoke(db, "/db/mom-data/xrx.user/" + userName)).isPresent());

    }

    @Test
    public void testGetCharterInstancesForImportedCharter() throws Exception {

        CharterAtomId id = new CharterAtomId("RS-IAGNS", "Charters", "F1_fasc.16_sub_N_1513");
        List<Charter> charters = db.getCharterInstances(id);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId(), id);

    }

    @Test
    public void testGetCharterInstancesForPrivateCharter() throws Exception {

        CharterAtomId id = new CharterAtomId("ea13e5f1-03b2-4bfa-9dd5-8fb770f98d7b", "46bc10f3-bc35-4fa8-ab82-25827dc243f6");
        List<Charter> charters = db.getCharterInstances(id);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId(), id);

    }

    @Test
    public void testGetCharterInstancesForPublishedPrivateCharter() throws Exception {

        CharterAtomId id = new CharterAtomId("f84fc6a2-85c6-4618-ab52-d0acfbcf58eb", "b94c19ed-95b2-40c6-9f0e-3f97d6e913ac");
        List<Charter> charters = db.getCharterInstances(id);
        assertEquals(charters.size(), 2);
        assertEquals(charters.get(0).getAtomId(), id);

    }

    @Test
    public void testGetCharterInstancesForSavedCharter() throws Exception {
        CharterAtomId id = new CharterAtomId("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_2");
        List<Charter> charters = db.getCharterInstances(id);
        assertEquals(charters.size(), 2);
        assertEquals(charters.get(0).getAtomId(), id);
    }

    @Test
    public void testGetCharterInstancesWithEncodeId() throws Exception {

        CharterAtomId id = new CharterAtomId("RS-IAGNS", "Charters", "IAGNS_F-.150_6605|193232"); // The | will be encoded
        List<Charter> charters = db.getCharterInstances(id);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId(), id);

    }

    @Test
    public void testGetGetCharterInstancesCharterNotExisting() throws Exception {
        CharterAtomId id = new CharterAtomId("RS-IAGNS", "Charters", "NotExisting");
        List<Charter> charters = db.getCharterInstances(id);
        assertTrue(charters.isEmpty());
    }

    @Test
    public void testGetGetCharterInstancesForPublishedCharter() throws Exception {

        CharterAtomId id = new CharterAtomId("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_1");
        List<Charter> charters = db.getCharterInstances(id);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId(), id);

    }

    @Test
    public void testGetUser() throws Exception {
        String userId = "user1.testuser@dev.monasterium.net";
        assertEquals(db.getUser(userId).get().getUserName(), userId);
    }

    @Test
    public void testGetUserWithNotExistingUser() throws Exception {
        String userId = "randomstuff@crazyness.uk";
        assertEquals(db.getUser(userId), Optional.empty());
    }

    @Test
    public void testInitializeUser() throws Exception {

        String parentCollection = "/db/system/security/exist/accounts";
        String newUserName = "newUser@dev.monasterium.net";
        String newUserPassword = "testing123";
        List<String> expectedGroups = new ArrayList<>(2);
        expectedGroups.add("atom");
        expectedGroups.add("guest");

        db.initializeUser(newUserName, newUserPassword);

        Optional<ExistResource> accountResource = callGetExistResourceMethod(newUserName + ".xml", parentCollection);

        assertTrue(accountResource.isPresent());
        assertEquals(accountResource.get().queryContentXml(XpathQuery.QUERY_CONFIG_NAME).get(0), newUserName);
        assertEquals(accountResource.get().queryContentXml(XpathQuery.QUERY_CONFIG_GROUP_NAME), expectedGroups);

        db.deleteExistUserAccount(newUserName);

    }

    @Test
    public void testIsUserInitialized() throws Exception {

        String userName = "madeo.anna@gmail.com";
        String newUserXml = "<xrx:user xmlns:xrx='http://www.monasterium.net/NS/xrx'> <xrx:username /> <xrx:password /> <xrx:firstname>Anna</xrx:firstname> <xrx:name>Madeo</xrx:name> <xrx:email>madeo.anna@gmail.com</xrx:email> <xrx:moderator>admin</xrx:moderator> <xrx:street /> <xrx:zip /> <xrx:town /> <xrx:phone /> <xrx:institution /> <xrx:info /> <xrx:storage> <xrx:saved_list /> <xrx:bookmark_list /> </xrx:storage> </xrx:user>";
        ExistResource userResource = new ExistResource(userName + ".xml", "/db/mom-data/xrx.user", newUserXml);
        db.storeExistResource(userResource);

        assertFalse(db.isUserInitialized(userName));
        assertTrue(db.isUserInitialized("admin"));

        db.deleteExistResource(userResource);

    }

    @Test
    public void testListUninitializedUserNames() throws Exception {

        String uninitializedUser = "user3.testuser@dev.monasterium.net";
        List<String> result = db.listUninitializedUserNames();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), uninitializedUser);

    }

    @Test
    public void testListUsers() throws Exception {
        assertEquals(db.listUserNames().size(), 4);
    }

    @Test
    public void testQueryDatabase() throws Exception {

        Class<?> cl = db.getClass();
        Method method = cl.getDeclaredMethod("queryDatabase", String.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> queryResults = (List<String>) method.invoke(db, QUERY_FACTORY.queryUserModerator("user1.testuser@dev.monasterium.net"));
        assertEquals(queryResults.get(0), "admin");

    }

    @Test
    public void testStoreExistResource() throws Exception {
        ExistResource res = new ExistResource("write@Test.xml", "/db", "<empty/>");
        db.storeExistResource(res);
        assertTrue(callGetExistResourceMethod(res.getResourceName(), res.getParentUri()).isPresent());
        db.deleteExistResource(res);
    }

    @Test
    public void testlistErroneouslySavedCharters() throws Exception {
        String userName = "admin";
        CharterAtomId erroneouslySavedCharter = new CharterAtomId("tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1");
        final List<CharterAtomId> erroneouslySavedCharterIds = db.listErroneouslySavedCharters(userName);
        assertEquals(erroneouslySavedCharterIds.size(), 1);
        assertEquals(erroneouslySavedCharterIds.get(0), erroneouslySavedCharter);
    }

    @NotNull
    private Optional<ExistResource> callGetExistResourceMethod(@NotNull String resourceName, @NotNull String parentCollection) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Class<?> cl = db.getClass();
        Method method = cl.getDeclaredMethod("getExistResource", String.class, String.class);
        method.setAccessible(true);
        //noinspection unchecked
        return (Optional<ExistResource>) method.invoke(db, resourceName, parentCollection);

    }

}