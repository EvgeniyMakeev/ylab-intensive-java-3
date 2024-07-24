package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dto.BookingAddDTO;
import dev.makeev.coworking_service_app.dto.BookingDTO;
import dev.makeev.coworking_service_app.exceptions.BookingNotFoundException;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailableException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;

import java.util.List;

/**
 * Service interface for managing bookings.
 */
public interface BookingService {

    /**
     * Adds a booking for a user.
     *
     * @param login the login of the user
     * @param bookingAddDTO the bookingAddDTO
     * @throws SpaceIsNotAvailableException if the space is not available for the specified date and time
     */
    void addBooking(String login, BookingAddDTO bookingAddDTO) throws SpaceIsNotAvailableException, SpaceNotFoundException;

    /**
     * Retrieves all bookings for a user.
     *
     * @param login the login of the user
     * @return a list of formatted booking strings
     */
    List<BookingDTO> getAllBookingsForUser(String login);

    /**
     * Retrieves all bookings sorted by user.
     *
     * @return a list of formatted booking strings
     */
    List<BookingDTO> getAllBookingsSortedByUser();

    /**
     * Deletes a booking by its index in the user's booking list.
     *
     * @param id the id of the booking for deleting
     */
    void deleteBookingById(String login, long id) throws BookingNotFoundException;

    void deleteBookingByIdByAdmin(String login, long id) throws BookingNotFoundException;
}
