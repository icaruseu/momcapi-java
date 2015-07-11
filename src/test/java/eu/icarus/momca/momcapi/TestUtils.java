package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static org.testng.Assert.assertNotNull;

/**
 * Created by daniel on 03.07.2015.
 */
class TestUtils {

    @NotNull
    private static final String SERVER_PROPERTIES_PATH = "/server.properties";
    @NotNull
    private static final String adminUser = "admin";
    @NotNull
    private static final String password = "momcapitest";

    @NotNull
    static MomcaConnection initMomcaConnection() throws MomcaException {

        URL serverPropertiesUrl = TestUtils.class.getResource(SERVER_PROPERTIES_PATH);
        assertNotNull(TestUtils.class.getResource(SERVER_PROPERTIES_PATH), "Test file missing");

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

        return new MomcaConnection(serverUrl, adminUser, password);

    }

}
