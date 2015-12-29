package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.util.Properties;

import static org.testng.Assert.assertNotNull;

/**
 * Created by daniel on 03.07.2015.
 */
public class TestUtils {

    @NotNull
    private static final String SERVER_PROPERTIES_PATH = "/server.properties";
    @NotNull
    private static final String adminUser = "admin";
    @NotNull
    private static final String password = "momcapitest";

    @NotNull
    public static Document getXmlFromResource(@NotNull String resourceName) throws ParsingException, IOException {

        try (InputStream is = Util.class.getClassLoader().getResourceAsStream(resourceName)) {
            Builder parser = new Builder();
            return parser.build(is);
        }

    }

    @NotNull
    public static ExistMomcaConnection initMomcaConnection() throws MomcaException {

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

        return new ExistMomcaConnection(serverUrl, adminUser, password);

    }

}
