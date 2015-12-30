package eu.icarus.momca.momcapi;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by daniel on 03.07.2015.
 */
public class TestUtils {

    @NotNull
    private static final String SERVER_PROPERTIES_PATH = "/connection.properties";
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

}
