package dev.makeev.coworking_service_app.dao.implementation;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dao.BookingDAO;
import dev.makeev.coworking_service_app.enums.SQLRequest;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.BookingRange;
import dev.makeev.coworking_service_app.model.WorkingHours;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The {@code BookingDAOInBd} class implements the {@link BookingDAO} interface.
 * It provides methods to interact with the database to manage Booking entities.
 */
@Component
public class BookingDAOInBd implements BookingDAO {

    private final DataSource dataSource;

    @Autowired
    public BookingDAOInBd(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public void add(Booking newBooking) {
        try (Connection connection = dataSource.getConnection()) {
            setAutoCommit(connection, false);
            try {
                long bookingId = addBooking(connection, newBooking);
                reserveSlots(connection, newBooking, bookingId);
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
     * Adds a new booking to the database.
     *
     * @param connection the database connection
     * @param newBooking the new booking to add
     * @return the generated booking ID
     */
    private long addBooking(Connection connection, Booking newBooking) {
        try (PreparedStatement addSpaceStatement = connection.prepareStatement(
                SQLRequest.ADD_BOOKING_SQL.getQuery(),
                Statement.RETURN_GENERATED_KEYS)) {
            addSpaceStatement.setString(1, newBooking.login());
            addSpaceStatement.setString(2, newBooking.nameOfBookingSpace());
            addSpaceStatement.setDate(3, Date.valueOf(newBooking.bookingRange().beginningBookingDate()));
            addSpaceStatement.setInt(4, newBooking.bookingRange().beginningBookingHour());
            addSpaceStatement.setDate(5, Date.valueOf(newBooking.bookingRange().endingBookingDate()));
            addSpaceStatement.setInt(6, newBooking.bookingRange().endingBookingHour());
            addSpaceStatement.executeUpdate();

            try (ResultSet generatedId = addSpaceStatement.getGeneratedKeys()) {
                if (generatedId.next()) {
                    return generatedId.getLong(1);
                } else {
                    throw new SQLException("Creating booking failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to add booking", e);
        }
    }

    /**
     * Updates the booking slots for a given booking.
     *
     * @param connection the database connection
     * @param newBooking the new booking
     * @param bookingId  the booking ID
     */
    private void reserveSlots(Connection connection, Booking newBooking, Long bookingId) {
        LocalDate startDate = newBooking.bookingRange().beginningBookingDate();
        LocalDate endDate = newBooking.bookingRange().endingBookingDate();
        int startHourBooking = newBooking.bookingRange().beginningBookingHour();
        int endHourBooking = newBooking.bookingRange().endingBookingHour();

        WorkingHours workingHours = getWorkingHoursOfSpaceByName(newBooking.nameOfBookingSpace());

        try (PreparedStatement updateSlotsStatement =
                     connection.prepareStatement(SQLRequest.BOOK_SLOTS_SQL.getQuery())) {

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                int startHour = workingHours.hourOfBeginningWorkingDay();
                int endHour = workingHours.hourOfEndingWorkingDay();

                if (date.isEqual(startDate)) {
                    startHour = startHourBooking;
                }
                if (date.isEqual(endDate)) {
                    endHour = endHourBooking;
                }

                for (int hour = startHour; hour < endHour; hour++) {
                    updateSlotsStatement.setLong(1, bookingId);
                    updateSlotsStatement.setString(2, newBooking.nameOfBookingSpace());
                    updateSlotsStatement.setDate(3, Date.valueOf(date));
                    updateSlotsStatement.setInt(4, hour);
                    updateSlotsStatement.addBatch();
                }
            }
            updateSlotsStatement.executeBatch();
        } catch (SQLException e) {
            throw new DaoException("Failed to booking slots", e);
        }
    }

    /**
     * Retrieves the working hours of a space identified by its name from the database.
     *
     * @param spaceName The name of the space to retrieve working hours for.
     * @return A {@link WorkingHours} object representing the working hours of the space.
     */
    private WorkingHours getWorkingHoursOfSpaceByName(String spaceName) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement getWorkingHoursStatement =
                     connection.prepareStatement(SQLRequest.GET_WORKING_HOURS_OF_SPACE_BY_NAME_SQL.getQuery())) {
            getWorkingHoursStatement.setString(1, spaceName);

            ResultSet resultSet = getWorkingHoursStatement.executeQuery();
            if (resultSet.next()) {
                int hourOfBeginning = resultSet.getInt("hour_of_beginning_working_day");
                int hourOfEnding = resultSet.getInt("hour_of_ending_working_day");
                resultSet.close();
                return new WorkingHours(hourOfBeginning, hourOfEnding);
            } else {
                resultSet.close();
                throw new DaoException("Space not found: " + spaceName, new SQLException());
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to get working hours of space", e);
        }
    }


    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public List<Booking> getAllForUser(String login) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     SQLRequest.GET_ALL_BOOKINGS_FOR_USER_SQL.getQuery())) {
            statement.setString(1, login);
            return getBookings(statement);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public List<Booking> getAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     SQLRequest.GET_ALL_BOOKINGS_SQL.getQuery())) {
            return getBookings(statement);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private List<Booking> getBookings(PreparedStatement statement) {
        try {
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
                        resultSet.getString("login"),
                        resultSet.getString("name_of_space"),
                        nextBookingRange);

                listOfTrainingsOfUser.add(nextBooking);
            }

            resultSet.close();
            return listOfTrainingsOfUser;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public Optional<Booking> getBookingById(long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQLRequest.GET_BOOKING_BY_ID_SQL.getQuery())) {

            statement.setLong(1, id);
            ResultSet statementResultSet = statement.executeQuery();

            if (statementResultSet.next()) {
                String login = statementResultSet.getString("login");
                String nameOfBookingSpace = statementResultSet.getString("name_of_space");
                BookingRange bookingRange = new BookingRange(
                        statementResultSet.getDate("beginning_booking_date").toLocalDate(),
                        statementResultSet.getInt("beginning_booking_hour"),
                        statementResultSet.getDate("ending_booking_date").toLocalDate(),
                        statementResultSet.getInt("ending_booking_hour"));

                statementResultSet.close();
                return Optional.of(new Booking(login, nameOfBookingSpace, bookingRange));
            } else {
                statementResultSet.close();
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
    public void delete(long idOfBooking) {
        try (Connection connection = dataSource.getConnection()) {
            setAutoCommit(connection, false);
            try {
                updateSlots(idOfBooking, connection);
                deleteBookingById(idOfBooking, connection);
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
     * Deletes a booking by its ID from the database.
     *
     * @param idOfBooking the ID of the booking to delete
     * @param connection  the database connection
     * @throws SQLException if booking not exist.
     */
    private static void deleteBookingById(long idOfBooking, Connection connection) throws SQLException {
        try (PreparedStatement statementDeleteBooking =
                     connection.prepareStatement(SQLRequest.DELETE_BOOKING_SQL.getQuery())) {
            statementDeleteBooking.setLong(1, idOfBooking);
            statementDeleteBooking.executeUpdate();
        }
    }

    /**
     * Updates the booking slots for a given booking ID.
     *
     * @param idOfBooking the ID of the booking
     * @param connection  the database connection
     * @throws SQLException if a database access error occurs
     */
    private static void updateSlots(long idOfBooking, Connection connection) throws SQLException {
        long availableForBooking = 0L;
        try (PreparedStatement statementUpdateSlots =
                     connection.prepareStatement(SQLRequest.UPDATE_SLOTS_SQL.getQuery())) {
            statementUpdateSlots.setLong(1, availableForBooking);
            statementUpdateSlots.setLong(2, idOfBooking);
            statementUpdateSlots.executeUpdate();
        }
    }
}
