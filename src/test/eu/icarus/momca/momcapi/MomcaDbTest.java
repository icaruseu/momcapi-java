package eu.icarus.momca.momcapi;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static org.testng.Assert.assertNotNull;

/**
 * Created by daniel on 25.06.2015.
 */
public class MomcaDbTest {

    private static final String SERVER_PROPERTIES_PATH = "/server.properties";
    String adminUser;
    String password;
    String serverUrl;

    @BeforeMethod
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
        adminUser = serverProperties.getProperty("adminUser");
        password = serverProperties.getProperty("password");

        assertNotNull(serverUrl, "'serverUrl' missing from '" + SERVER_PROPERTIES_PATH + "'");
        assertNotNull(adminUser, "'adminUser' missing from '" + SERVER_PROPERTIES_PATH + "'");
        assertNotNull(password, "'password' missing from '" + SERVER_PROPERTIES_PATH + "'");

    }

    @Test
    public void testGetImportedCharter() throws Exception {

    }

    @Test
    public void testGetPublishedCharter() throws Exception {

    }

    @Test
    public void testGetSavedCharter() throws Exception {

    }

    @Test
    public void testGetUser() throws Exception {

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