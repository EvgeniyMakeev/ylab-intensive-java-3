package dev.makeev.coworking_service_app.util;

import dev.makeev.coworking_service_app.exceptions.DaoException;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for initializing the database using Liquibase.
 */
@RequiredArgsConstructor
public final class InitDb {

    private final DataSource dataSource;
    private final String schemaName;
    private final String changelog;

    /**
     * Initializes the database schema and performs Liquibase migration.
     */
    public void initDb() {
        createSchema(schemaName);
        try (Connection connection = dataSource.getConnection()) {
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

    /**
     * Creates the database schema if it does not already exist.
     *
     * @param schemaName the name of the schema to create
     */
    private void createSchema(String schemaName) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
