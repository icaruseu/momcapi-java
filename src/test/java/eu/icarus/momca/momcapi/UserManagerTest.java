package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.resource.ExistResource;
import eu.icarus.momca.momcapi.model.resource.User;
import eu.icarus.momca.momcapi.query.XpathQuery;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
        IdUser id = new IdUser(userName);
        String password = "newPassword";
        String moderator = "admin";
        userManager.addUser(userName, password, moderator);

        assertTrue(userManager.getUser(id).isPresent());
        assertTrue(userManager.isUserInitialized(id));

        userManager.deleteUser(id);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddUserWithWrongModerator() throws Exception {

        String userName = "newUser@dev.monasterium.net";
        String password = "newPassword";
        String moderator = "notExistingModerator";
        userManager.addUser(userName, password, moderator);

    }

    @Test
    public void testChangeModerator() throws Exception {

        String userName = "modUpdateUser";

        User oldModerator = userManager.getUser(new IdUser("admin")).get();
        User newModerator = userManager.getUser(new IdUser("user1.testuser@dev.monasterium.net")).get();

        User user = userManager.addUser(userName, "", oldModerator.getIdentifier());
        User updatedUser = userManager.changeModerator(user, newModerator);

        assertEquals(updatedUser.getIdModerator().getIdentifier(), newModerator.getIdentifier());

        userManager.deleteUser(updatedUser.getId());

    }

    @Test
    public void testDeleteExistUserAccount() throws Exception {

        String userName = "removeAccountTest@dev.monasterium.net";
        String password = "testing123";
        String moderator = "admin";
        User user = userManager.addUser(userName, password, moderator);
        userManager.deleteExistUserAccount(userName);

        assertFalse(userManager.isUserInitialized(new IdUser(userName)));

        userManager.deleteUser(user.getId());

    }

    @Test
    public void testDeleteUser() throws Exception {

        String userName = "removeUserTest@dev.monasterium.net";
        IdUser id = new IdUser(userName);
        String password = "testing123";
        String moderator = "admin";

        userManager.addUser(userName, password, moderator);
        userManager.deleteUser(id);

        assertFalse(userManager.getUser(id).isPresent());
        assertFalse(userManager.isUserInitialized(id));
        assertFalse(momcaConnection.getCollection("/db/mom-data/xrx.user/" + userName).isPresent());

    }

    @Test
    public void testGetUser() throws Exception {

        String userName = "user1.testuser@dev.monasterium.net";
        String moderator = "admin";
        User user = userManager.getUser(new IdUser(userName)).get();
        assertEquals(user.getIdentifier(), userName);
        assertEquals(user.getIdModerator().getIdentifier(), moderator);
        assertTrue(user.isInitialized());

    }

    @Test
    public void testGetUserWithNotExistingUser() throws Exception {
        String userId = "randomstuff@crazyness.uk";
        assertEquals(userManager.getUser(new IdUser(userId)), Optional.empty());
    }

    @Test
    public void testInitializeUser() throws Exception {

        // create user resource
        String newUserName = "testuser@gmail.com";
        String newUserXml = "<xrx:user xmlns:xrx='http://www.monasterium.net/NS/xrx'> <xrx:username /> <xrx:password /> <xrx:firstname>Anna</xrx:firstname> <xrx:name>Madeo</xrx:name> <xrx:email>testuser@gmail.com</xrx:email> <xrx:moderator>admin</xrx:moderator> <xrx:street /> <xrx:zip /> <xrx:town /> <xrx:phone /> <xrx:institution /> <xrx:info /> <xrx:storage> <xrx:saved_list /> <xrx:bookmark_list /> </xrx:storage> </xrx:user>";
        ExistResource userResource = new ExistResource(newUserName + ".xml", "/db/mom-data/xrx.user", newUserXml);
        momcaConnection.storeExistResource(userResource);

        // create new user without using momcaConnection.addUser()
        String parentCollection = "/db/system/security/exist/accounts";
        String newUserPassword = "testing123";
        List<String> expectedGroups = new ArrayList<>(2);
        expectedGroups.add("atom");
        expectedGroups.add("guest");
        User user = new User(userResource);

        // initialize user
        User initializedUser = userManager.initializeUser(user.getId(), newUserPassword);
        assertTrue(initializedUser.isInitialized());

        // test initialization success directly in the database
        Optional<ExistResource> resourceOptional = momcaConnection.getExistResource(newUserName + ".xml", parentCollection);
        assertTrue(resourceOptional.isPresent());
        ExistResource res = resourceOptional.get();
        Method queryContentXml = ExistResource.class.getDeclaredMethod("queryContentAsList", XpathQuery.class);
        queryContentXml.setAccessible(true);
        //noinspection unchecked
        assertEquals(((List<String>) queryContentXml.invoke(res, XpathQuery.QUERY_CONFIG_NAME)).get(0), newUserName);
        //noinspection unchecked
        assertEquals(((List<String>) queryContentXml.invoke(res, XpathQuery.QUERY_CONFIG_GROUP_NAME)), expectedGroups);

        // clean up
        userManager.deleteUser(initializedUser.getId());

    }

    @Test
    public void testIsUserInitialized() throws Exception {
        assertFalse(userManager.isUserInitialized(new IdUser("uninitialized.testuser@dev.monasterium.net")));
        assertTrue(userManager.isUserInitialized(new IdUser("admin")));
    }

    @Test
    public void testListUsers() throws Exception {
        assertEquals(userManager.listUsers().size(), 4);
        assertTrue(userManager.listUsers().contains(new IdUser("admin")));
    }

}