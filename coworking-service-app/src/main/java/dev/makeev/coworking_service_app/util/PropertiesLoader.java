package dev.makeev.coworking_service_app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for loading application properties from a file named "application.properties".
 * This class uses the class loader to retrieve the properties file from the application's resources.
 */
public class PropertiesLoader {

    /**
     * Loads properties from the "application.properties" file located in the application's resources.
     *
     * @return Properties object containing the loaded properties
     * @throws RuntimeException If there is an IOException while loading the properties file
     */
    public static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = PropertiesLoader.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
        return properties;
    }
}
