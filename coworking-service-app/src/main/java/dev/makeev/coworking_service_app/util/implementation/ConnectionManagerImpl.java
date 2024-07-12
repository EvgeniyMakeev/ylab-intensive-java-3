package dev.makeev.coworking_service_app.util.implementation;

import dev.makeev.coworking_service_app.util.ConnectionManager;
import dev.makeev.coworking_service_app.util.PropertiesLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The {@code ConnectionManagerImpl} class implements the {@link ConnectionManager} interface.
 * It provides methods to open a database connection using the JDBC DriverManager.
 */
@Component
public final class ConnectionManagerImpl implements ConnectionManager {

    private final String url;
    private final String username;
    private final String password;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load PostgreSQL driver", e);
        }
    }

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
    public ConnectionManagerImpl(@Value("${db.url}") String url,
                                 @Value("${db.username}") String username,
                                 @Value("${db.password}") String password) {
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