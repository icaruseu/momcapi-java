package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.atomid.CharterAtomId;
import eu.icarus.momca.momcapi.existquery.ExistQuery;
import eu.icarus.momca.momcapi.existquery.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.Charter;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.testng.Assert.*;

/**
 * Created by daniel on 25.06.2015.
 */
public class MomCATest {

    private static final ExistQueryFactory QUERY_FACTORY = new ExistQueryFactory();
    private static final String SERVER_PROPERTIES_PATH = "/server.properties";
    private static final String adminUser = "admin";
    private static final String password = "momcapitest";
    private MomCA db;
    private String serverUrl;

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

        CharterAtomId id = new CharterAtomId("RS-IAGNS", "Charters", "F1_fasc.16_sub_N_1513");
        List<Charter> charters = db.getImportedCharter(id);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId(), id);

    }

    @Test
    public void testGetImportedCharterWithEncodableId() throws Exception {

        CharterAtomId id = new CharterAtomId("RS-IAGNS", "Charters", "IAGNS_F-.150_6605|193232"); // The | gets encoded on the import process when used for the atom:id
        List<Charter> charters = db.getImportedCharter(id);
        assertEquals(charters.size(), 1);
        charters.get(0).getAtomId().equals(id);
        assertEquals(charters.get(0).getAtomId(), id);

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

        Class<?> cl = db.getClass();
        Method method = cl.getDeclaredMethod("queryDatabase", ExistQuery.class);
        method.setAccessible(true);

        ExistQuery query = QUERY_FACTORY.queryUserModerator("user1.testuser@dev.monasterium.net");

        List<String> queryResults = (List<String>) method.invoke(db, query);
        assertEquals(queryResults.get(0), "admin");

    }

}