package dev.makeev.coworking_service_app.dao.implementation;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.enums.SQLRequest;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.WorkingHours;
import dev.makeev.coworking_service_app.util.ConnectionManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * An in-memory implementation of the {@link SpaceDAO} interface.
 */
public final class SpaceDAOInBd implements SpaceDAO {

    private final ConnectionManager connectionManager;

    public SpaceDAOInBd(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void add(Space newSpace) {
        try (var connection = connectionManager.open()) {
            connection.setAutoCommit(false);
            try {
                addSpace(newSpace, connection);
                addSlots(newSpace, connection);
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    throw new DaoException("Rollback failed", rollbackException);
                }
                throw new DaoException("SQL error occurred", e);
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new DaoException("Failed to set autoCommit back to true", e);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to open connection", e);
        }
    }

    private static void addSpace(Space newSpace, Connection connection) throws SQLException {
        try (var addSpaceStatement = connection.prepareStatement(SQLRequest.ADD_SPACE_SQL.getQuery())) {
            addSpaceStatement.setString(1, newSpace.name());
            addSpaceStatement.setInt(2, newSpace.workingHours().hourOfBeginningWorkingDay());
            addSpaceStatement.setInt(3, newSpace.workingHours().hourOfEndingWorkingDay());
            addSpaceStatement.executeUpdate();
        }
    }

    private static void addSlots(Space newSpace, Connection connection) throws SQLException {
        try (var addSlotsForBookingStatement = connection.prepareStatement(SQLRequest.ADD_SLOTS_SQL.getQuery())) {
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
    @Override
    public List<String> getNamesOfSpaces() {
        try (var connection = connectionManager.open();
             var statement = connection.prepareStatement(SQLRequest.GET_ALL_SPACES_SQL.getQuery())) {
            List<String> listNamesOfSpaces = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                listNamesOfSpaces.add(resultSet.getString("name"));
            }
            return listNamesOfSpaces;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public Optional<Space> getSpaceByName(String nameOfSpace) {
        try (var connection = connectionManager.open();
             var spaceStatement = connection.prepareStatement(SQLRequest.GET_SPACE_BY_NAME_SQL.getQuery());
             var slotsStatement = connection.prepareStatement(SQLRequest.GET_SLOTS_BY_SPACE_NAME_SQL.getQuery())) {

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

                return Optional.of(new Space(nameOfSpace, workingHours, bookingSlots));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Error retrieving space by name", e);
        }
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void delete(String nameOfSpace) {
        try (var connection = connectionManager.open()) {
            connection.setAutoCommit(false);
            try {
                deleteByName(connection, SQLRequest.DELETE_BOOKING_FOR_SPACE_SQL, nameOfSpace);
                deleteByName(connection, SQLRequest.DELETE_SLOTS_FOR_SPACE_SQL, nameOfSpace);
                deleteByName(connection, SQLRequest.DELETE_SPACE_SQL, nameOfSpace);
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    throw new DaoException("Rollback failed", rollbackException);
                }
                throw new DaoException("SQL error occurred", e);
            } finally {
                try {
                    connection.setAutoCommit(false);
                } catch (SQLException e) {
                    throw new DaoException("Failed to set autoCommit back to true", e);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to open connection", e);
        }
    }

    private static void deleteByName(Connection connection, SQLRequest deleteBookingForSpaceSql, String nameOfSpace) throws SQLException {
        try (var statement = connection.prepareStatement(deleteBookingForSpaceSql.getQuery())) {
            statement.setString(1, nameOfSpace);
            statement.executeUpdate();
        }
    }
}
