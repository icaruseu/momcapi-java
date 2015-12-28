package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.resource.ExistResource;
import eu.icarus.momca.momcapi.model.resource.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
        assertTrue(userManager.add(new User(userName, moderator), password));

        assertTrue(userManager.get(id).isPresent());
        assertTrue(userManager.isInitialized(id));

        userManager.delete(id);

        userName = "newUser@dev.monasterium.net";
        password = "newPassword";
        moderator = "notExistingModerator";
        assertFalse(userManager.add(new User(userName, moderator), password));

    }

    @Test
    public void testChangeModerator() throws Exception {

        String userName = "modUpdateUser";

        User newModerator = userManager.get(new IdUser("user1.testuser@dev.monasterium.net")).get();

        userManager.add(new User(userName, "admin"), "");

        User originalUser = userManager.get(new IdUser(userName)).get();
        originalUser.setModerator(newModerator.getIdentifier());

        assertTrue(userManager.changeModerator(originalUser.getId(), newModerator.getId()));
        assertEquals(userManager.get(originalUser.getId()).get().getIdModerator().getIdentifier(), newModerator.getIdentifier());

        userManager.delete(originalUser.getId());

    }

    @Test
    public void testChangeUserPassword() throws Exception {

        String userName = "user10@dev.monasterium.net";
        String moderator = "admin";
        IdUser idUser = new IdUser(userName);

        userManager.add(new User(userName, moderator), "");

        User user = userManager.get(idUser).get();

        assertTrue(userManager.changeUserPassword(user, "newPassword"));

        userManager.delete(idUser);

    }

    @Test
    public void testDeleteExistUserAccount() throws Exception {

        String userName = "removeAccountTest@dev.monasterium.net";
        String password = "testing123";
        String moderator = "admin";
        userManager.add(new User(userName, moderator), password);
        User user = userManager.get(new IdUser(userName)).get();
        userManager.deleteExistUserAccount(userName);

        assertFalse(userManager.isInitialized(new IdUser(userName)));

        userManager.delete(user.getId());

    }

    @Test
    public void testDeleteUser() throws Exception {

        String userName = "removeUserTest@dev.monasterium.net";
        IdUser id = new IdUser(userName);
        String password = "testing123";
        String moderator = "admin";

        userManager.add(new User(userName, moderator), password);
        assertTrue(userManager.delete(id));

        assertFalse(userManager.get(id).isPresent());
        assertFalse(userManager.isInitialized(id));
        assertFalse(momcaConnection.readCollection("/db/mom-data/xrx.user/" + userName).isPresent());

    }

    @Test
    public void testGetUser() throws Exception {

        String userName = "user1.testuser@dev.monasterium.net";
        String moderator = "admin";

        User user = userManager.get(new IdUser(userName)).get();

        assertEquals(user.getIdentifier(), userName);
        assertEquals(user.getIdModerator().getIdentifier(), moderator);

    }

    @Test
    public void testGetUserWithNotExistingUser() throws Exception {
        String userId = "randomstuff@crazyness.uk";
        assertEquals(userManager.get(new IdUser(userId)), Optional.empty());
    }

    @Test
    public void testInitializeUser() throws Exception {

        // create user resource
        String newUserName = "testuser@gmail.com";
        String newUserXml = "<xrx:user xmlns:xrx='http://www.monasterium.net/NS/xrx'> <xrx:username /> <xrx:password /> <xrx:firstname>Anna</xrx:firstname> <xrx:name>Madeo</xrx:name> <xrx:email>testuser@gmail.com</xrx:email> <xrx:moderator>admin</xrx:moderator> <xrx:street /> <xrx:zip /> <xrx:town /> <xrx:phone /> <xrx:institution /> <xrx:info /> <xrx:storage> <xrx:saved_list /> <xrx:bookmark_list /> </xrx:storage> </xrx:user>";
        ExistResource userResource = new ExistResource(newUserName + ".xml", "/db/mom-data/xrx.user", newUserXml);
        momcaConnection.writeExistResource(userResource);

        // create new user without using momcaConnection.add()
        String newUserPassword = "testing123";
        User user = new User(userResource);

        // initialize user
        assertTrue(userManager.initialize(user.getId(), newUserPassword));
        assertTrue(userManager.isInitialized(user.getId()));

        // clean up
        userManager.delete(user.getId());

    }

    @Test
    public void testIsExisting() throws Exception {
        assertTrue(userManager.isExisting(new IdUser("user1.testuser@dev.monasterium.net")));
        assertFalse(userManager.isExisting(new IdUser("user17.testuser@dev.monasterium.net")));
    }

    @Test
    public void testIsInitialized() throws Exception {
        assertFalse(userManager.isInitialized(new IdUser("uninitialized.testuser@dev.monasterium.net")));
        assertTrue(userManager.isInitialized(new IdUser("admin")));
    }

    @Test
    public void testListUsers() throws Exception {
        assertEquals(userManager.list().size(), 4);
        assertTrue(userManager.list().contains(new IdUser("admin")));
    }

}