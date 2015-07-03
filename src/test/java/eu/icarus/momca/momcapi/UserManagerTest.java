package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.resource.ExistResource;
import eu.icarus.momca.momcapi.resource.XpathQuery;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

/**
 * Created by daniel on 03.07.2015.
 */
public class UserManagerTest {

    private MomcaConnection momcaConnection;
    private UserManager userManager;

    @BeforeClass
    public void setUp() throws Exception {
        momcaConnection = TestUtils.initMomcaConnection();
        userManager = momcaConnection.getUserManager();
        assertNotNull(userManager, "MOM-CA connection not initialized.");
    }

    @Test
    public void testAddUser() throws Exception {

        String userName = "newlyAddedUser@dev.monasterium.net";
        String password = "newPassword";
        String moderator = "admin";
        userManager.addUser(userName, password, moderator);

        assertTrue(userManager.getUser(userName).isPresent());
        assertTrue(userManager.isUserInitialized(userName));

        userManager.deleteUser(userName);

    }

    @Test
    public void testDeleteExistUserAccount() throws Exception {

        String userName = "removeAccountTest@dev.monasterium.net";
        String password = "testing123";
        userManager.initializeUser(userName, password);

        userManager.deleteExistUserAccount(userName);

        assertFalse(userManager.isUserInitialized(userName));

    }

    @Test
    public void testDeleteUser() throws Exception {

        String userName = "removeUserTest@dev.monasterium.net";
        String password = "testing123";
        String moderator = "admin";

        userManager.addUser(userName, password, moderator);
        userManager.deleteUser(userName);

        assertFalse(userManager.getUser(userName).isPresent());
        assertFalse(userManager.isUserInitialized(userName));
        assertFalse(momcaConnection.getCollection("/db/mom-data/xrx.user/" + userName).isPresent());

    }

    @Test
    public void testGetUser() throws Exception {
        String userId = "user1.testuser@dev.monasterium.net";
        assertEquals(userManager.getUser(userId).get().getUserName(), userId);
    }

    @Test
    public void testGetUserWithNotExistingUser() throws Exception {
        String userId = "randomstuff@crazyness.uk";
        assertEquals(userManager.getUser(userId), Optional.empty());
    }

    @Test
    public void testInitializeUser() throws Exception {

        String parentCollection = "/db/system/security/exist/accounts";
        String newUserName = "newUser@dev.monasterium.net";
        String newUserPassword = "testing123";
        List<String> expectedGroups = new ArrayList<>(2);
        expectedGroups.add("atom");
        expectedGroups.add("guest");

        userManager.initializeUser(newUserName, newUserPassword);

        Optional<ExistResource> accountResource = callGetExistResourceMethod(newUserName + ".xml", parentCollection);

        assertTrue(accountResource.isPresent());
        assertEquals(accountResource.get().queryContentXml(XpathQuery.QUERY_CONFIG_NAME).get(0), newUserName);
        assertEquals(accountResource.get().queryContentXml(XpathQuery.QUERY_CONFIG_GROUP_NAME), expectedGroups);

        userManager.deleteExistUserAccount(newUserName);

    }

    @Test
    public void testIsUserInitialized() throws Exception {

        String userName = "madeo.anna@gmail.com";
        String newUserXml = "<xrx:user xmlns:xrx='http://www.monasterium.net/NS/xrx'> <xrx:username /> <xrx:password /> <xrx:firstname>Anna</xrx:firstname> <xrx:name>Madeo</xrx:name> <xrx:email>madeo.anna@gmail.com</xrx:email> <xrx:moderator>admin</xrx:moderator> <xrx:street /> <xrx:zip /> <xrx:town /> <xrx:phone /> <xrx:institution /> <xrx:info /> <xrx:storage> <xrx:saved_list /> <xrx:bookmark_list /> </xrx:storage> </xrx:user>";
        ExistResource userResource = new ExistResource(userName + ".xml", "/db/mom-data/xrx.user", newUserXml);
        momcaConnection.storeExistResource(userResource);

        assertFalse(userManager.isUserInitialized(userName));
        assertTrue(userManager.isUserInitialized("admin"));

        momcaConnection.deleteExistResource(userResource);

    }

    @Test
    public void testListUninitializedUserNames() throws Exception {

        String uninitializedUser = "user3.testuser@dev.monasterium.net";
        List<String> result = userManager.listUninitializedUserNames();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), uninitializedUser);

    }

    @Test
    public void testListUsers() throws Exception {
        assertEquals(userManager.listUserNames().size(), 4);
    }

    @NotNull
    private Optional<ExistResource> callGetExistResourceMethod(@NotNull String resourceName, @NotNull String parentCollection) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Class<?> cl = momcaConnection.getClass();
        Method method = cl.getDeclaredMethod("getExistResource", String.class, String.class);
        method.setAccessible(true);
        //noinspection unchecked
        return (Optional<ExistResource>) method.invoke(momcaConnection, resourceName, parentCollection);

    }

}