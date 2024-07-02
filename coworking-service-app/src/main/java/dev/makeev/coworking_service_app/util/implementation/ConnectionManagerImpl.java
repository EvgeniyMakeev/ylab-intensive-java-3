package dev.makeev.coworking_service_app.util.implementation;

import dev.makeev.coworking_service_app.util.ConnectionManager;
import dev.makeev.coworking_service_app.util.PropertiesLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The {@code ConnectionManagerImpl} class implements the {@link ConnectionManager} interface.
 * It provides methods to open a database connection using the JDBC DriverManager.
 */
public final class ConnectionManagerImpl implements ConnectionManager {

    private final String url;
    private final String username;
    private final String password;

    /**
     * Constructs a new {@code ConnectionManagerImpl} and initializes it with database connection
     * properties from the "application.properties" file on the classpath.
     */
    public ConnectionManagerImpl() {
        Properties properties = PropertiesLoader.loadProperties();
        this.url = properties.getProperty("db.url");
        this.username = properties.getProperty("db.username");
        this.password = properties.getProperty("db.password");
    }

    /**
     * Constructs a new {@code ConnectionManagerImpl} with the specified database connection properties.
     *
     * @param url      The URL of the database to connect to.
     * @param username The username for the database connection.
     * @param password The password for the database connection.
     */
    public ConnectionManagerImpl(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public Connection open() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to open connection", e);
        }
    }
}