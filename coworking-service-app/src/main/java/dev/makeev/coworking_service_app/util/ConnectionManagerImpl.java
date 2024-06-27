package dev.makeev.coworking_service_app.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The {@code ConnectionManagerImpl} class implements the {@link ConnectionManager} interface.
 * It provides methods to open a database connection using the JDBC DriverManager.
 */
public class ConnectionManagerImpl implements ConnectionManager {

    private String url;
    private String username;
    private String password;

    /**
     * Constructs a new {@code ConnectionManagerImpl} and initializes it with database connection
     * properties from the "application.properties" file on the classpath.
     */
    public ConnectionManagerImpl() {
        Properties properties = new Properties();
        try (InputStream inputStream = ConnectionManagerImpl.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            properties.load(inputStream);
            this.url = properties.getProperty("db.url");
            this.username = properties.getProperty("db.username");
            this.password = properties.getProperty("db.password");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public Connection open() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to open connection", e);
        }
    }
}