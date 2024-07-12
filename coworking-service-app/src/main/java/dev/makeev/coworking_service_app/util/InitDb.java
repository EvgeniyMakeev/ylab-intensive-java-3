package dev.makeev.coworking_service_app.util;

import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.util.implementation.ConnectionManagerImpl;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public final class InitDb {

    private final ConnectionManager connectionManager = new ConnectionManagerImpl();

    public void initDb() {
        Properties properties = PropertiesLoader.loadProperties();
        String changelogPath = properties.getProperty("liquibase.changelogFile");
        String defaultSchemaName = properties.getProperty("liquibase.defaultSchemaName");
        createSchema(defaultSchemaName);
        try (Connection connection = connectionManager.open()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(defaultSchemaName);

            try (Liquibase liquibase = new Liquibase(changelogPath, new ClassLoaderResourceAccessor(), database)) {
                liquibase.clearCheckSums();
                liquibase.update(new Contexts(), new LabelExpression());
                System.out.println("Migration is completed successfully");
            } catch (LiquibaseException e) {
                System.out.println("Liquibase Exception in migration: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception in migration: " + e.getMessage());
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

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
