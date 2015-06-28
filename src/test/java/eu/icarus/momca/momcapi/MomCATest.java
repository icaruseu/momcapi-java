package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.atomid.CharterAtomId;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;

import static org.testng.Assert.*;

/**
 * Created by daniel on 25.06.2015.
 */
public class MomCATest {

    private static final String SERVER_PROPERTIES_PATH = "/server.properties";
    private static final String adminUser = "admin";
    private static final String password = "momcapitest";
    MomCA db;
    String serverUrl;

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

        serverUrl = serverProperties.getProperty("serverUrl");

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
    public void testGetImportedCharter() throws Exception {

    }

    @Test
    public void testGetPublishedCharter() throws Exception {
        CharterAtomId id = new CharterAtomId("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_1");
        assertEquals(db.getPublishedCharter(id).get().getAtomId(), id);
    }

    @Test
    public void testGetPublishedCharterNotExisting() throws Exception {
        CharterAtomId id = new CharterAtomId("CH-KAE", "Urkunden", "ABCDEFG");
        assertFalse(db.getPublishedCharter(id).isPresent());
    }

    @Test
    public void testGetSavedCharter() throws Exception {
        CharterAtomId id = new CharterAtomId("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_2");
        assertEquals(db.getSavedCharter(id).get().getAtomId(), id);
    }

    @Test
    public void testGetSavedCharterNotExisting() throws Exception {
        CharterAtomId id = new CharterAtomId("CH-KAE", "Urkunden", "ABCDEFG");
        assertFalse(db.getSavedCharter(id).isPresent());
    }

    @Test
    public void testGetUser() throws Exception {
        String userId = "user1.testuser@dev.monasterium.net";
        assertEquals(db.getUser(userId).get().getUserId(), userId);
    }

    @Test
    public void testGetUserWithNotExistingUser() throws Exception {
        String userId = "randomstuff@crazyness.uk";
        assertEquals(db.getUser(userId), Optional.empty());
    }

    @Test
    public void testListUsers() throws Exception {
        assertTrue(db.listUsers().size() == 3);
    }

    @Test
    public void testQueryDatabase() throws Exception {
        String queryModeratorOfUser1 = "declare namespace xrx='http://www.monasterium.net/NS/xrx'; collection('/db/mom-data/xrx.user')/xrx:user[.//xrx:email='user1.testuser@dev.monasterium.net']/xrx:moderator/text()";
        assertEquals(db.queryDatabase(queryModeratorOfUser1).get(0), "admin");
    }

}