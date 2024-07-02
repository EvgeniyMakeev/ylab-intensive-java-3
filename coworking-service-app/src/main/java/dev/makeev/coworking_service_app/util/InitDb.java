package dev.makeev.coworking_service_app.util;

import dev.makeev.coworking_service_app.exceptions.DaoException;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * The {@code InitDb} class provides a method to initialize the database schema
 * and apply migrations using Liquibase.
 */
public final class InitDb {

    private final ConnectionManager connectionManager;

    /**
     * Constructs a new {@code InitDb} object with the specified {@code ConnectionManager}.
     *
     * @param connectionManager The {@code ConnectionManager} to use for database connections.
     */
    public InitDb(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Initializes the database schema and applies migrations using Liquibase.
     * If the "non_public" schema does not exist, it creates it.
     */
    public void initDb() {
        Properties properties =  PropertiesLoader.loadProperties();
        String changelogPath = properties.getProperty("liquibase.changelogFile");

        String defaultSchemaName = properties.getProperty("liquibase.defaultSchemaName");
        createSchema(defaultSchemaName);

        try (Connection connection = connectionManager.open()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(defaultSchemaName);
            Liquibase liquibase = new Liquibase(changelogPath,
                    new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("Migration is completed successfully");
        } catch (LiquibaseException | SQLException e) {
            System.out.println("SQL Exception in migration " + e.getMessage());
        }
    }

    /**
     * Creates the specified schema in the database if it does not already exist.
     *
     * @param schemaName The name of the schema to create.
     * @throws DaoException if a SQL error occurs while creating the schema.
     */
    private void createSchema(String schemaName) {
        try (Connection connection = connectionManager.open()) {
            String sql = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

}
