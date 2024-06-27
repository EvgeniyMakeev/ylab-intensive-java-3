package dev.makeev.coworking_service_app.dao;

import dev.makeev.coworking_service_app.model.Booking;

import java.util.List;

/**
 * DAO interface for managing bookings.
 */
public interface BookingDAO {
    /**
     * Adds a booking for a user.
     *
     * @param booking the booking to be added
     */
    void add(Booking booking);

    /**
     * Retrieves all bookings for a user.
     *
     * @param loginOfUser the login of the user
     * @return a list of bookings for the user
     */
    List<Booking> getAllForUser(String loginOfUser);

    /**
     * Retrieves all bookings.
     *
     * @return a list of bookings
     */
    List<Booking> getAll();

    /**
     * Deletes a booking by user login and booking ID.
     *
     * @param id the ID of the booking to be deleted
     */
    void delete(long id);
}