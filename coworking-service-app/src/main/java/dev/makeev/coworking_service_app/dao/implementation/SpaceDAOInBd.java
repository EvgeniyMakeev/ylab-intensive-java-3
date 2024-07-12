package dev.makeev.coworking_service_app.dao.implementation;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.enums.SQLRequest;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.WorkingHours;
import dev.makeev.coworking_service_app.util.ConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

/**
 * The {@code SpaceDAOInBd} class implements the {@link SpaceDAO} interface.
 * It provides methods to interact with the database to manage Space entities.
 */
@Component
public class SpaceDAOInBd implements SpaceDAO {

    private final ConnectionManager connectionManager;

    @Autowired
    public SpaceDAOInBd(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public void add(Space newSpace) {
        try (Connection connection = connectionManager.open()) {
            setAutoCommit(connection, false);
            try {
                addSpace(newSpace, connection);
                addSlots(newSpace, connection);
                connection.commit();
            } catch (SQLException e) {
                rollback(connection);
                throw new DaoException("SQL error occurred", e);
            } finally {
                setAutoCommit(connection, true);
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to open connection", e);
        }
    }

    /**
     * Rolls back the given SQL connection.
     *
     * @param connection The SQL connection to be rolled back.
     * @throws DaoException If the rollback operation fails.
     */
    private void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            throw new DaoException("Rollback failed", rollbackException);
        }
    }

    /**
     * Sets the auto-commit mode of the given SQL connection.
     *
     * @param connection The SQL connection for which to set the auto-commit mode.
     * @param autoCommit The desired auto-commit mode (true or false).
     * @throws DaoException If setting the auto-commit mode fails.
     */
    private void setAutoCommit(Connection connection, boolean autoCommit) {
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new DaoException("Failed to set autoCommit to " + autoCommit, e);
        }
    }

    /**
     * Adds a new space to the database.
     *
     * @param newSpace the new space to add
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    private static void addSpace(Space newSpace, Connection connection) throws SQLException {
        try (PreparedStatement addSpaceStatement = connection.prepareStatement(SQLRequest.ADD_SPACE_SQL.getQuery())) {
            addSpaceStatement.setString(1, newSpace.name());
            addSpaceStatement.setInt(2, newSpace.workingHours().hourOfBeginningWorkingDay());
            addSpaceStatement.setInt(3, newSpace.workingHours().hourOfEndingWorkingDay());
            addSpaceStatement.executeUpdate();
        }
    }

    /**
     * Adds booking slots for a new space to the database.
     *
     * @param newSpace the new space
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    private static void addSlots(Space newSpace, Connection connection) throws SQLException {
        try (PreparedStatement addSlotsForBookingStatement = connection.prepareStatement(SQLRequest.ADD_SLOTS_SQL.getQuery())) {
            newSpace.bookingSlots().forEach((date, bookingSlots) -> IntStream.range(newSpace.workingHours().hourOfBeginningWorkingDay(),
                    newSpace.workingHours().hourOfEndingWorkingDay()).forEach(hour -> {
                try {
                    addSlotsForBookingStatement.setString(1, newSpace.name());
                    addSlotsForBookingStatement.setDate(2, Date.valueOf(date));
                    addSlotsForBookingStatement.setInt(3, hour);
                    addSlotsForBookingStatement.setLong(4, 0L);
                    addSlotsForBookingStatement.addBatch();
                } catch (SQLException e) {
                    throw new DaoException("Failed to add slot to batch", e);
                }
            }));
            addSlotsForBookingStatement.executeBatch();
        }
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public List<String> getNamesOfSpaces() {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SQLRequest.GET_ALL_SPACES_SQL.getQuery())) {
            List<String> listNamesOfSpaces = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                listNamesOfSpaces.add(resultSet.getString("name"));
            }

            resultSet.close();

            return listNamesOfSpaces;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public Optional<Space> getSpaceByName(String nameOfSpace) {
        try (Connection connection = connectionManager.open();
             PreparedStatement spaceStatement = connection.prepareStatement(SQLRequest.GET_SPACE_BY_NAME_SQL.getQuery());
             PreparedStatement slotsStatement = connection.prepareStatement(SQLRequest.GET_SLOTS_BY_SPACE_NAME_SQL.getQuery())) {

            spaceStatement.setString(1, nameOfSpace);
            ResultSet spaceStatementResultSet = spaceStatement.executeQuery();

            if (spaceStatementResultSet.next()) {
                WorkingHours workingHours = new WorkingHours(
                        spaceStatementResultSet.getInt("hour_of_beginning_working_day"),
                        spaceStatementResultSet.getInt("hour_of_ending_working_day"));

                slotsStatement.setString(1, nameOfSpace);
                ResultSet slotsStatementResultSet = slotsStatement.executeQuery();

                Map<LocalDate, Map<Integer, Long>> bookingSlots = new HashMap<>();
                while (slotsStatementResultSet.next()) {
                    LocalDate date = slotsStatementResultSet.getDate("date").toLocalDate();
                    int hour = slotsStatementResultSet.getInt("hour");
                    long bookingId = slotsStatementResultSet.getLong("booking_id");

                    bookingSlots
                            .computeIfAbsent(date, k -> new HashMap<>())
                            .put(hour, bookingId);
                }

                spaceStatementResultSet.close();
                return Optional.of(new Space(nameOfSpace, workingHours, bookingSlots));
            } else {
                spaceStatementResultSet.close();
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DaoException("Error retrieving space by name", e);
        }
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public void delete(String nameOfSpace) {
        try (Connection connection = connectionManager.open()) {
            setAutoCommit(connection, false);
            try {
                deleteByName(connection, SQLRequest.DELETE_BOOKING_FOR_SPACE_SQL, nameOfSpace);
                deleteByName(connection, SQLRequest.DELETE_SLOTS_FOR_SPACE_SQL, nameOfSpace);
                deleteByName(connection, SQLRequest.DELETE_SPACE_SQL, nameOfSpace);
                connection.commit();
            } catch (SQLException e) {
                rollback(connection);
                throw new DaoException("SQL error occurred", e);
            } finally {
                setAutoCommit(connection, true);
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to open connection", e);
        }
    }

    /**
     * Deletes an entity by name using a specified SQL request.
     *
     * @param connection the database connection
     * @param deleteBookingForSpaceSql the SQL request to delete the entity
     * @param nameOfSpace the name of the entity to delete
     * @throws SQLException if a database access error occurs
     */
    private static void deleteByName(Connection connection, SQLRequest deleteBookingForSpaceSql, String nameOfSpace) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(deleteBookingForSpaceSql.getQuery())) {
            statement.setString(1, nameOfSpace);
            statement.executeUpdate();
        }
    }
}
