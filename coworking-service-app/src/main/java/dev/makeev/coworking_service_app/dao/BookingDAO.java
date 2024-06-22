package dev.makeev.coworking_service_app.dao;

import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.UserBooking;

import java.util.List;
import java.util.Map;

/**
 * DAO interface for managing bookings.
 */
public interface BookingDAO {
    /**
     * Adds a booking for a user.
     *
     * @param loginOfUser the login of the user
     * @param booking the booking to be added
     */
    void add(String loginOfUser, Booking booking);

    /**
     * Retrieves all bookings for a user.
     *
     * @param loginOfUser the login of the user
     * @return a list of bookings for the user
     */
    List<UserBooking> getAllForUser(String loginOfUser);

    /**
     * Retrieves all bookings.
     *
     * @return a map of user logins to their bookings
     */
    Map<String, List<UserBooking>> getAll();

    /**
     * Deletes a booking by user login and booking ID.
     *
     * @param loginOfUser the login of the user
     * @param id the ID of the booking to be deleted
     */
    void delete(String loginOfUser, long id);
}