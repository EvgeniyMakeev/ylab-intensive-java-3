package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.aop.annotations.LoggingTime;
import dev.makeev.coworking_service_app.aop.annotations.LoggingToDb;
import dev.makeev.coworking_service_app.dao.BookingDAO;
import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.exceptions.BookingNotFoundException;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailableException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.BookingRange;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.WorkingHours;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Service class for managing bookings.
 */
public final class BookingService {

    private final BookingDAO bookingDAO;
    private final SpaceDAO spaceDAO;

    /**
     * Constructs a new BookingService.
     *
     * @param bookingDAO the BookingDAO to use for booking operations
     * @param spaceDAO the SpaceDAO to use for space operations
     */
    public BookingService(BookingDAO bookingDAO, SpaceDAO spaceDAO) {
        this.bookingDAO = bookingDAO;
        this.spaceDAO = spaceDAO;
    }

    /**
     * Adds a booking for a user.
     *
     * @param loginOfUser the login of the user
     * @param booking the booking
     * @throws SpaceIsNotAvailableException if the space is not available for the specified date and time
     */
    @LoggingTime
    @LoggingToDb
    public void addBooking(String loginOfUser, Booking booking) throws SpaceIsNotAvailableException, SpaceNotFoundException {

        Space bookingSpace = spaceDAO.getSpaceByName(booking.nameOfBookingSpace()).orElseThrow(SpaceNotFoundException::new);

        if (isSpaceAvailableForBookingOnDateAndTime(bookingSpace, booking.bookingRange(), bookingSpace.workingHours())) {
            bookingDAO.add(booking);

        } else {
            throw new SpaceIsNotAvailableException();
        }
    }

    /**
     * Checks if a space is available for booking on a specified date and time.
     *
     * @param bookingSpace the space to check
     * @param bookingRange the range of the booking
     * @param workingHours the working hours of the space
     * @return true if the space is available, false otherwise
     */
    private Boolean isSpaceAvailableForBookingOnDateAndTime(Space bookingSpace, BookingRange bookingRange, WorkingHours workingHours) {
        if (!isValidDateAndTimeOfBooking(bookingSpace, bookingRange, workingHours)) {
            return false;
        }

        return bookingRange.beginningBookingDate().datesUntil(bookingRange.endingBookingDate().plusDays(1))
                .allMatch(date -> {
                    Map<Integer, Long> slots = bookingSpace.bookingSlots().get(date);
                    int startHour = (date.equals(bookingRange.beginningBookingDate())) ?
                            bookingRange.beginningBookingHour() : workingHours.hourOfBeginningWorkingDay();
                    int endHour = (date.equals(bookingRange.endingBookingDate())) ?
                            bookingRange.endingBookingHour() : workingHours.hourOfEndingWorkingDay();

                    return IntStream.range(startHour, endHour).allMatch(hour -> slots.get(hour) == 0L);
                });
    }

    /**
     * Validates the date and time of a booking.
     *
     * @param bookingSpace the space to check
     * @param bookingRange the range of the booking
     * @param workingHours the working hours of the space
     * @return true if the date and time are valid, false otherwise
     */
    private static boolean isValidDateAndTimeOfBooking(Space bookingSpace, BookingRange bookingRange, WorkingHours workingHours) {
        if (bookingRange.beginningBookingDate().isBefore(LocalDate.now())) {
            return false;
        } else if (bookingRange.beginningBookingHour() < workingHours.hourOfBeginningWorkingDay() ||
                bookingRange.endingBookingHour() > workingHours.hourOfEndingWorkingDay()) {
            return false;
        } else return bookingSpace.bookingSlots().containsKey(bookingRange.beginningBookingDate()) &&
                bookingSpace.bookingSlots().containsKey(bookingRange.endingBookingDate());
    }

    /**
     * Retrieves all bookings for a user.
     *
     * @param loginOfUser the login of the user
     * @return a list of formatted booking strings
     */
    @LoggingTime
    @LoggingToDb
    public List<Booking> getAllBookingsForUser(String loginOfUser) {
        return bookingDAO.getAllForUser(loginOfUser);
    }

    /**
     * Retrieves all bookings sorted by user.
     *
     * @return a list of formatted booking strings
     */
    @LoggingTime
    public List<Booking> getAllBookingsSortedByUser() {
        return bookingDAO.getAll().stream()
                .sorted(Comparator.comparing(Booking::loginOfUser))
                .toList();
    }


    /**
     * Deletes a booking by its index in the user's booking list.
     *
     * @param bookingId the id of the booking for deleting
     */
    @LoggingTime
    @LoggingToDb
    public void deleteBookingById(String login, long bookingId) throws BookingNotFoundException {
        if (bookingDAO.getBookingById(bookingId).isPresent()) {
            bookingDAO.delete(bookingId);
        } else {
            throw new BookingNotFoundException();
        }
    }
}