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
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class InitDb {

    @Value("${liquibase.defaultSchemaName}")
    private String schemaName;

    @Value("${liquibase.changelogFile}")
    private String changelog;

    private ConnectionManager connectionManager = new ConnectionManagerImpl();

    public InitDb() {
    }

    public InitDb(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public void initDb() {
        createSchema(schemaName);
        try (Connection connection = connectionManager.open()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(schemaName);

            try (Liquibase liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database)) {
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
