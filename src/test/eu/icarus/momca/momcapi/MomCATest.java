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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by daniel on 25.06.2015.
 */
public class MomCATest {

    private static final String SERVER_PROPERTIES_PATH = "/server.properties";
    private static final String adminUser = "admin";
    MomCA db;
    String password;
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
        password = serverProperties.getProperty("password");

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
        assertEquals(db.getPublishedCharter(id).get().getAtomId(), id,
                "The from the read charter has to match the provided.");
    }

    @Test
    public void testGetPublishedCharterWithNotExistingCharter() throws Exception {
        CharterAtomId id = new CharterAtomId("CH-KAE", "Urkunden", "ABCDEFG");
        assertEquals(db.getPublishedCharter(id), Optional.empty(),
                "The charter read from the database has to be empty.");
    }

    @Test
    public void testGetSavedCharter() throws Exception {

    }

    @Test
    public void testGetUser() throws Exception {
        String userId = "admin";
        assertEquals(db.getUser(userId).get().getUserId(), userId,
                "The user id read from the user in the database must match the provided.");
    }

    @Test
    public void testGetUserWithNotExistingUser() throws Exception {
        String userId = "randomstuff@crazyness.uk";
        assertEquals(db.getUser(userId), Optional.empty(),
                "The user read from the database must be empty.");
    }

    @Test
    public void testListUserResourceNames() throws Exception {

    }

    @Test
    public void testListUsers() throws Exception {

    }

    @Test
    public void testQueryDatabase() throws Exception {

    }

}