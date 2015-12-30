package eu.icarus.momca.momcapi;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by djell on 30/12/2015.
 */
@SuppressWarnings("AccessCanBeTightened")
public class MomcaConnectionFactory {

    public static final String PASSWORD_PROPERTY = "password";
    public static final String SERVER_PROPERTIES_NAME = "server.properties";
    public static final String SERVER_TYPE_PROPERTY = "serverType";
    public static final String SERVER_URL_PROPERTY = "serverUrl";
    public static final String USER_PROPERTY = "user";
    private static final Logger LOGGER = LoggerFactory.getLogger(MomcaConnectionFactory.class);

    @NotNull
    public static MomcaConnection get() {

        LOGGER.info("Trying to get MOM-CA instance.");

        MomcaConnection momcaConnection;

        Properties properties = new Properties();

        try {
            LOGGER.debug("Try to read properties file '{}'.", SERVER_PROPERTIES_NAME);
            properties.load(MomcaConnectionFactory.class.getClassLoader().getResourceAsStream(SERVER_PROPERTIES_NAME));
            LOGGER.debug("Properties read from '{}'.", SERVER_PROPERTIES_NAME);

        } catch (IOException e) {
            LOGGER.error("Failed to read properties file '{}'.", SERVER_PROPERTIES_NAME, e);
        }

        MomcaConnectionType type = MomcaConnectionType.valueOf(properties.getProperty(SERVER_TYPE_PROPERTY).toUpperCase());
        String url = properties.getProperty(SERVER_URL_PROPERTY);
        String userName = properties.getProperty(USER_PROPERTY);
        String password = properties.getProperty(PASSWORD_PROPERTY);


        switch (type) {
            case EXIST:
            default:
                momcaConnection = new ExistMomcaConnection(url, userName, password);
        }

        LOGGER.info("Returning '{}'-based MOM-CA instance.", type);

        return momcaConnection;

    }

    private enum MomcaConnectionType {EXIST}

}
