package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.advice.annotations.LoggingToDb;
import dev.makeev.coworking_service_app.exceptions.BookingNotFoundException;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailableException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;
import dev.makeev.coworking_service_app.model.Booking;

import java.util.List;

public interface BookingService {
    @LoggingTime
    @LoggingToDb
    void addBooking(String login, Booking booking) throws SpaceIsNotAvailableException, SpaceNotFoundException;

    @LoggingTime
    @LoggingToDb
    List<Booking> getAllBookingsForUser(String login);

    @LoggingTime
    List<Booking> getAllBookingsSortedByUser();

    @LoggingTime
    @LoggingToDb
    void deleteBookingById(String login, long bookingId) throws BookingNotFoundException;
}
