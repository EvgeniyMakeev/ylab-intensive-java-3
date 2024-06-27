package dev.makeev.coworking_service_app.dao.implementation;

import dev.makeev.coworking_service_app.dao.BookingDAO;
import dev.makeev.coworking_service_app.enums.SQLRequest;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.BookingRange;
import dev.makeev.coworking_service_app.util.ConnectionManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * An in-memory implementation of the {@link BookingDAO} interface.
 */
public final class BookingDAOInBd implements BookingDAO {

    private final ConnectionManager connectionManager;

    public BookingDAOInBd(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void add(Booking newBooking) {
        try (var connection = connectionManager.open()) {
            connection.setAutoCommit(false);
            try {
                addBooking(connection, newBooking);
                bookingSlots(connection, newBooking);
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

    private static void addBooking(Connection connection, Booking newBooking) throws SQLException {
        try (var addSpaceStatement = connection.prepareStatement(SQLRequest.ADD_BOOKING_SQL.getQuery())) {
            addSpaceStatement.setString(1, newBooking.loginOfUser());
            addSpaceStatement.setString(2, newBooking.nameOfBookingSpace());
            addSpaceStatement.setDate(3, Date.valueOf(newBooking.bookingRange().beginningBookingDate()));
            addSpaceStatement.setInt(4, newBooking.bookingRange().beginningBookingHour());
            addSpaceStatement.setDate(5, Date.valueOf(newBooking.bookingRange().endingBookingDate()));
            addSpaceStatement.setInt(6, newBooking.bookingRange().endingBookingHour());
            addSpaceStatement.executeUpdate();
        }
    }

    private static void bookingSlots(Connection connection, Booking newBooking) throws SQLException {
        try (var updateSlotsStatement = connection.prepareStatement(SQLRequest.BOOK_SLOTS_SQL.getQuery())) {
            newBooking.bookingRange().beginningBookingDate().datesUntil(newBooking.bookingRange().endingBookingDate().plusDays(1))
                    .forEach(date -> {
                        for (int hour = newBooking.bookingRange().beginningBookingHour();
                             hour <= newBooking.bookingRange().endingBookingHour();
                             hour++) {
                            try {
                                updateSlotsStatement.setLong(1, newBooking.id());
                                updateSlotsStatement.setDate(2, Date.valueOf(date));
                                updateSlotsStatement.setInt(3, hour);
                                updateSlotsStatement.addBatch();
                            } catch (SQLException e) {
                                throw new DaoException("Failed to add slot to batch", e);
                            }
                        }
                    });

            updateSlotsStatement.executeBatch();
        }
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public List<Booking> getAllForUser(String loginOfUser) {
        try (var connection = connectionManager.open();
             var statement = connection.prepareStatement(
                     SQLRequest.GET_ALL_BOOKINGS_FOR_USER_SQL.getQuery())) {
            statement.setString(1, loginOfUser);
            ResultSet resultSet = statement.executeQuery();
            List<Booking> listOfTrainingsOfUser = new ArrayList<>();
            while (resultSet.next()) {
                BookingRange nextBookingRange = new BookingRange(
                        resultSet.getDate("beginning_booking_date").toLocalDate(),
                        resultSet.getInt("beginning_booking_hour"),
                        resultSet.getDate("ending_booking_date").toLocalDate(),
                        resultSet.getInt("ending_booking_hour"));

                Booking nextBooking = new Booking(
                        resultSet.getLong("id"),
                        loginOfUser,
                        resultSet.getString("name_of_space"),
                        nextBookingRange);

                listOfTrainingsOfUser.add(nextBooking);
            }
            return listOfTrainingsOfUser;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public List<Booking> getAll() {
        try (var connection = connectionManager.open();
             var statement = connection.prepareStatement(
                     SQLRequest.GET_ALL_BOOKINGS_SQL.getQuery())) {
            ResultSet resultSet = statement.executeQuery();
            List<Booking> listOfTrainingsOfUser = new ArrayList<>();
            while (resultSet.next()) {
                BookingRange nextBookingRange = new BookingRange(
                        resultSet.getDate("beginning_booking_date").toLocalDate(),
                        resultSet.getInt("beginning_booking_hour"),
                        resultSet.getDate("ending_booking_date").toLocalDate(),
                        resultSet.getInt("ending_booking_hour"));

                Booking nextBooking = new Booking(
                        resultSet.getLong("id"),
                        resultSet.getString("login_of_user"),
                        resultSet.getString("name_of_space"),
                        nextBookingRange);

                listOfTrainingsOfUser.add(nextBooking);
            }
            return listOfTrainingsOfUser;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void delete(long idOfBooking) {
        try (var connection = connectionManager.open()) {
            connection.setAutoCommit(false);
            try {
                updateSlots(idOfBooking, connection);
                deleteBookingById(idOfBooking, connection);
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

    private static void deleteBookingById(long idOfBooking, Connection connection) throws SQLException {
        try (var statementDeleteBooking =
                     connection.prepareStatement(SQLRequest.DELETE_BOOKING_SQL.getQuery())) {
            statementDeleteBooking.setLong(1, idOfBooking);
            statementDeleteBooking.executeUpdate();
        }
    }

    private static void updateSlots(long idOfBooking, Connection connection) throws SQLException {
        long availableForBooking = 0L;
        try (var statementUpdateSlots =
                     connection.prepareStatement(SQLRequest.UPDATE_SLOTS_SQL.getQuery())) {
            statementUpdateSlots.setLong(1, availableForBooking);
            statementUpdateSlots.setLong(1, idOfBooking);
            statementUpdateSlots.executeUpdate();
        }
    }
}
