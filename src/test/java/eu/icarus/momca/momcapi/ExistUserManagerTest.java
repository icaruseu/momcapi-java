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
public class ExistUserManagerTest {

    private ExistUserManager existUserManager;
    private MomcaConnection momcaConnection;

    @BeforeClass
    public void setUp() throws Exception {
        momcaConnection = TestUtils.initMomcaConnection();
        existUserManager = (ExistUserManager) momcaConnection.getUserManager();
        assertNotNull(existUserManager, "MOM-CA connection not initialized.");
    }

    @Test
    public void testAddUser() throws Exception {

        String userName = "newlyAddedUser@dev.monasterium.net";
        IdUser id = new IdUser(userName);
        String password = "newPassword";
        String moderator = "admin";
        assertTrue(existUserManager.add(new User(userName, moderator), password));

        assertTrue(existUserManager.get(id).isPresent());
        assertTrue(existUserManager.isInitialized(id));

        existUserManager.delete(id);

        userName = "newUser@dev.monasterium.net";
        password = "newPassword";
        moderator = "notExistingModerator";
        assertFalse(existUserManager.add(new User(userName, moderator), password));

    }

    @Test
    public void testChangeModerator() throws Exception {

        String userName = "modUpdateUser";

        User newModerator = existUserManager.get(new IdUser("user1.testuser@dev.monasterium.net")).get();

        existUserManager.add(new User(userName, "admin"), "");

        User originalUser = existUserManager.get(new IdUser(userName)).get();
        originalUser.setModerator(newModerator.getIdentifier());

        assertTrue(existUserManager.changeModerator(originalUser.getId(), newModerator.getId()));
        assertEquals(existUserManager.get(originalUser.getId()).get().getIdModerator().getIdentifier(), newModerator.getIdentifier());

        existUserManager.delete(originalUser.getId());

    }

    @Test
    public void testChangeUserPassword() throws Exception {

        String userName = "user10@dev.monasterium.net";
        String moderator = "admin";
        IdUser idUser = new IdUser(userName);

        existUserManager.add(new User(userName, moderator), "");

        User user = existUserManager.get(idUser).get();

        assertTrue(existUserManager.changeUserPassword(user, "newPassword"));

        existUserManager.delete(idUser);

    }

    @Test
    public void testDeleteExistUserAccount() throws Exception {

        String userName = "removeAccountTest@dev.monasterium.net";
        String password = "testing123";
        String moderator = "admin";
        existUserManager.add(new User(userName, moderator), password);
        User user = existUserManager.get(new IdUser(userName)).get();
        existUserManager.deleteExistUserAccount(userName);

        assertFalse(existUserManager.isInitialized(new IdUser(userName)));

        existUserManager.delete(user.getId());

    }

    @Test
    public void testDeleteUser() throws Exception {

        String userName = "removeUserTest@dev.monasterium.net";
        IdUser id = new IdUser(userName);
        String password = "testing123";
        String moderator = "admin";

        existUserManager.add(new User(userName, moderator), password);
        assertTrue(existUserManager.delete(id));

        assertFalse(existUserManager.get(id).isPresent());
        assertFalse(existUserManager.isInitialized(id));
        assertFalse(((ExistMomcaConnection) momcaConnection).readCollection("/db/mom-data/xrx.user/" + userName).isPresent());

    }

    @Test
    public void testGetUser() throws Exception {

        String userName = "user1.testuser@dev.monasterium.net";
        String moderator = "admin";

        User user = existUserManager.get(new IdUser(userName)).get();

        assertEquals(user.getIdentifier(), userName);
        assertEquals(user.getIdModerator().getIdentifier(), moderator);

    }

    @Test
    public void testGetUserWithNotExistingUser() throws Exception {
        String userId = "randomstuff@crazyness.uk";
        assertEquals(existUserManager.get(new IdUser(userId)), Optional.empty());
    }

    @Test
    public void testInitializeUser() throws Exception {

        // create user resource
        String newUserName = "testuser@gmail.com";
        String newUserXml = "<xrx:user xmlns:xrx='http://www.monasterium.net/NS/xrx'> <xrx:username /> <xrx:password /> <xrx:firstname>Anna</xrx:firstname> <xrx:name>Madeo</xrx:name> <xrx:email>testuser@gmail.com</xrx:email> <xrx:moderator>admin</xrx:moderator> <xrx:street /> <xrx:zip /> <xrx:town /> <xrx:phone /> <xrx:institution /> <xrx:info /> <xrx:storage> <xrx:saved_list /> <xrx:bookmark_list /> </xrx:storage> </xrx:user>";
        ExistResource userResource = new ExistResource(newUserName + ".xml", "/db/mom-data/xrx.user", newUserXml);
        ((ExistMomcaConnection) momcaConnection).writeExistResource(userResource);

        // create new user without using momcaConnection.add()
        String newUserPassword = "testing123";
        User user = new User(userResource);

        // initialize user
        assertTrue(existUserManager.initialize(user.getId(), newUserPassword));
        assertTrue(existUserManager.isInitialized(user.getId()));

        // clean up
        existUserManager.delete(user.getId());

    }

    @Test
    public void testIsExisting() throws Exception {
        assertTrue(existUserManager.isExisting(new IdUser("user1.testuser@dev.monasterium.net")));
        assertFalse(existUserManager.isExisting(new IdUser("user17.testuser@dev.monasterium.net")));
    }

    @Test
    public void testIsInitialized() throws Exception {
        assertFalse(existUserManager.isInitialized(new IdUser("uninitialized.testuser@dev.monasterium.net")));
        assertTrue(existUserManager.isInitialized(new IdUser("admin")));
    }

    @Test
    public void testListUsers() throws Exception {
        assertEquals(existUserManager.list().size(), 4);
        assertTrue(existUserManager.list().contains(new IdUser("admin")));
    }

}