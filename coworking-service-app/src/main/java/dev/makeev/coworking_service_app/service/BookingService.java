package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.advice.annotations.LoggingToDb;
import dev.makeev.coworking_service_app.dto.BookingAddDTO;
import dev.makeev.coworking_service_app.dto.BookingDTO;
import dev.makeev.coworking_service_app.exceptions.BookingNotFoundException;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailableException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;

import java.util.List;

public interface BookingService {
    @LoggingTime
    @LoggingToDb
    void addBooking(String login, BookingAddDTO bookingAddDTO) throws SpaceIsNotAvailableException, SpaceNotFoundException;

    @LoggingTime
    @LoggingToDb
    List<BookingDTO> getAllBookingsForUser(String login);

    @LoggingTime
    List<BookingDTO> getAllBookingsSortedByUser();

    @LoggingTime
    @LoggingToDb
    void deleteBookingById(String login, long bookingId) throws BookingNotFoundException;
}
