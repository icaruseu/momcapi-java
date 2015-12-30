package eu.icarus.momca.momcapi;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Gets instances of MomcaConnection.
 */
@SuppressWarnings("AccessCanBeTightened")
public class MomcaConnectionFactory {

    public static final String PASSWORD_PROPERTY = "password";
    public static final String SERVER_PROPERTIES_NAME = "connection.properties";
    public static final String SERVER_TYPE_PROPERTY = "connectionType";
    public static final String SERVER_URL_PROPERTY = "connectionUrl";
    public static final String USER_PROPERTY = "user";
    private static final Logger LOGGER = LoggerFactory.getLogger(MomcaConnectionFactory.class);

    private MomcaConnection momcaConnection = null;

    @NotNull
    private MomcaConnection createMomcaConnection() {

        LOGGER.info("Creating new MOM-CA connection instance.");

        MomcaConnection momcaConnection;

        Properties properties = new Properties();

        try {
            LOGGER.debug("Try to read properties file '{}'.", SERVER_PROPERTIES_NAME);
            properties.load(this.getClass().getClassLoader().getResourceAsStream(SERVER_PROPERTIES_NAME));
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

        LOGGER.info("{}-based MOM-CA connection instantiated.", type);

        return momcaConnection;

    }

    /**
     * @return An instance of the MOM-CA connection as set up in 'server.properties'.
     */
    @NotNull
    public MomcaConnection getMomcaConnection() {

        LOGGER.info("Trying to get the MOM-CA connection instance.");

        if (momcaConnection == null) {
            this.momcaConnection = createMomcaConnection();
        }

        LOGGER.info("Returning the MOM-CA connection instance '{}'.", this.momcaConnection);

        return this.momcaConnection;

    }

    private enum MomcaConnectionType {EXIST}

}
