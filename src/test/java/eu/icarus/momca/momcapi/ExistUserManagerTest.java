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

    private MomcaConnection mc;
    private ExistUserManager um;

    @BeforeClass
    public void setUp() throws Exception {
        MomcaConnectionFactory factory = new MomcaConnectionFactory();
        mc = factory.getMomcaConnection();
        um = (ExistUserManager) mc.getUserManager();
        assertNotNull(um, "MOM-CA connection not initialized.");
    }

    @Test
    public void testAddUser() throws Exception {

        String userName = "newlyAddedUser@dev.monasterium.net";
        IdUser id = new IdUser(userName);
        String password = "newPassword";
        String moderator = "admin";
        assertTrue(um.add(new User(userName, moderator), password));

        assertTrue(um.get(id).isPresent());
        assertTrue(um.isInitialized(id));

        um.delete(id);

        userName = "newUser@dev.monasterium.net";
        password = "newPassword";
        moderator = "notExistingModerator";
        assertFalse(um.add(new User(userName, moderator), password));

    }

    @Test
    public void testChangeModerator() throws Exception {

        String userName = "modUpdateUser";

        User newModerator = um.get(new IdUser("user1.testuser@dev.monasterium.net")).get();

        um.add(new User(userName, "admin"), "");

        User originalUser = um.get(new IdUser(userName)).get();
        originalUser.setModerator(newModerator.getIdentifier());

        assertTrue(um.changeModerator(originalUser.getId(), newModerator.getId()));
        assertEquals(um.get(originalUser.getId()).get().getIdModerator().getIdentifier(), newModerator.getIdentifier());

        um.delete(originalUser.getId());

    }

    @Test
    public void testChangeUserPassword() throws Exception {

        String userName = "user10@dev.monasterium.net";
        String moderator = "admin";
        IdUser idUser = new IdUser(userName);

        um.add(new User(userName, moderator), "");

        User user = um.get(idUser).get();

        assertTrue(um.changeUserPassword(user, "newPassword"));

        um.delete(idUser);

    }

    @Test
    public void testDeleteExistUserAccount() throws Exception {

        String userName = "removeAccountTest@dev.monasterium.net";
        String password = "testing123";
        String moderator = "admin";
        um.add(new User(userName, moderator), password);
        User user = um.get(new IdUser(userName)).get();
        um.deleteExistUserAccount(userName);

        assertFalse(um.isInitialized(new IdUser(userName)));

        um.delete(user.getId());

    }

    @Test
    public void testDeleteUser() throws Exception {

        String userName = "removeUserTest@dev.monasterium.net";
        IdUser id = new IdUser(userName);
        String password = "testing123";
        String moderator = "admin";

        um.add(new User(userName, moderator), password);
        assertTrue(um.delete(id));

        assertFalse(um.get(id).isPresent());
        assertFalse(um.isInitialized(id));
        assertFalse(((ExistMomcaConnection) mc).readCollection("/db/mom-data/xrx.user/" + userName).isPresent());

    }

    @Test
    public void testGetUser() throws Exception {

        String userName = "user1.testuser@dev.monasterium.net";
        String moderator = "admin";

        User user = um.get(new IdUser(userName)).get();

        assertEquals(user.getIdentifier(), userName);
        assertEquals(user.getIdModerator().getIdentifier(), moderator);

    }

    @Test
    public void testGetUserWithNotExistingUser() throws Exception {
        String userId = "randomstuff@crazyness.uk";
        assertEquals(um.get(new IdUser(userId)), Optional.empty());
    }

    @Test
    public void testInitializeUser() throws Exception {

        // create user resource
        String newUserName = "testuser@gmail.com";
        String newUserXml = "<xrx:user xmlns:xrx='http://www.monasterium.net/NS/xrx'> <xrx:username /> <xrx:password /> <xrx:firstname>Anna</xrx:firstname> <xrx:name>Madeo</xrx:name> <xrx:email>testuser@gmail.com</xrx:email> <xrx:moderator>admin</xrx:moderator> <xrx:street /> <xrx:zip /> <xrx:town /> <xrx:phone /> <xrx:institution /> <xrx:info /> <xrx:storage> <xrx:saved_list /> <xrx:bookmark_list /> </xrx:storage> </xrx:user>";
        ExistResource userResource = new ExistResource(newUserName + ".xml", "/db/mom-data/xrx.user", newUserXml);
        ((ExistMomcaConnection) mc).writeExistResource(userResource);

        // create new user without using mc.add()
        String newUserPassword = "testing123";
        User user = new User(userResource);

        // initialize user
        assertTrue(um.initialize(user.getId(), newUserPassword));
        assertTrue(um.isInitialized(user.getId()));

        // clean up
        um.delete(user.getId());

    }

    @Test
    public void testIsExisting() throws Exception {
        assertTrue(um.isExisting(new IdUser("user1.testuser@dev.monasterium.net")));
        assertFalse(um.isExisting(new IdUser("user17.testuser@dev.monasterium.net")));
    }

    @Test
    public void testIsInitialized() throws Exception {
        assertFalse(um.isInitialized(new IdUser("uninitialized.testuser@dev.monasterium.net")));
        assertTrue(um.isInitialized(new IdUser("admin")));
    }

    @Test
    public void testListUsers() throws Exception {
        assertEquals(um.list().size(), 4);
        assertTrue(um.list().contains(new IdUser("admin")));
    }

}