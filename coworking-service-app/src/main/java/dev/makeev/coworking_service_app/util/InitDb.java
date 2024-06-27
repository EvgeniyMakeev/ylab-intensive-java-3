package dev.makeev.coworking_service_app.util;

import dev.makeev.coworking_service_app.exceptions.DaoException;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.SQLException;

/**
 * The {@code InitDb} class provides a method to initialize the database schema
 * and apply migrations using Liquibase.
 */
public class InitDb {

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
        try (var connection = connectionManager.open()) {
            String sql = "CREATE SCHEMA IF NOT EXISTS non_public";
            var statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DaoException(e);
        }

        try (var connection = connectionManager.open()) {
            var database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName("non_public");
            var liquibase = new Liquibase("db/changelog/changelog.xml",
                    new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("Migration is completed successfully");
        } catch (LiquibaseException | SQLException e) {
            System.out.println("SQL Exception in migration " + e.getMessage());
        }
    }
}

//        spaceService.addAndUpdateSpace("Workplace No. 1", 8, 20, 20);
//        spaceService.addAndUpdateSpace("Conference hall", 10, 18, 10);
//
//        bookingService.addBooking("User1", "Workplace No. 1",
//                LocalDate.now(), 8, LocalDate.now().plusDays(2), 15);
//        bookingService.addBooking("User1", "Workplace No. 1",
//                LocalDate.now().plusDays(5), 10, LocalDate.now().plusDays(5), 18);
//        bookingService.addBooking("User1", "Conference hall",
//                LocalDate.now().plusDays(1), 11, LocalDate.now().plusDays(4), 15);
//
//        bookingService.addBooking("User2", "Workplace No. 1",
//                LocalDate.now().plusDays(3), 11, LocalDate.now().plusDays(3), 14);
//        bookingService.addBooking("User2", "Workplace No. 1",
//                LocalDate.now().plusDays(11), 8, LocalDate.now().plusDays(13), 20);
//        bookingService.addBooking("User2", "Conference hall",
//                LocalDate.now().plusDays(8), 11, LocalDate.now().plusDays(12), 15);
//    }
//}
