package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.BookingDAO;
import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailableException;
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
     * @param bookingSpaceName the name of the space to book
     * @param beginningBookingDate the start date of the booking
     * @param beginningBookingHour the start hour of the booking
     * @param endingBookingDate the endingBookingHour date of the booking
     * @param endingBookingHour the endingBookingHour hour of the booking
     * @throws SpaceIsNotAvailableException if the space is not available for the specified date and time
     */
    public void addBooking(String loginOfUser, String bookingSpaceName, LocalDate beginningBookingDate, int beginningBookingHour,
                           LocalDate endingBookingDate, int endingBookingHour) throws SpaceIsNotAvailableException {

        Space bookingSpace = spaceDAO.getSpaceByName(bookingSpaceName).orElseThrow();
        BookingRange bookingRange = new BookingRange(beginningBookingDate, beginningBookingHour, endingBookingDate, endingBookingHour);

        if (isSpaceAvailableForBookingOnDateAndTime(bookingSpace, bookingRange, bookingSpace.workingHours())) {
            bookingDAO.add(new Booking(loginOfUser, bookingSpaceName, bookingRange));

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

                    return IntStream.range(startHour, endHour).allMatch(hour -> slots.getOrDefault(hour, 0L) == 0L);
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
    public List<Booking> getAllBookingsForUser(String loginOfUser) {
        return bookingDAO.getAllForUser(loginOfUser);
    }

    /**
     * Retrieves all bookings sorted by user.
     *
     * @return a list of formatted booking strings
     */
    public List<Booking> getAllBookingsSortedByUser() {
        return bookingDAO.getAll().stream()
                .sorted(Comparator.comparing(Booking::loginOfUser))
                .toList();
    }

    /**
     * Retrieves all bookings sorted by date.
     *
     * @return a list of formatted booking strings
     */
    public List<String> getAllBookingsSortedByDate() {
        List<Booking> bookings = bookingDAO.getAll();
        return bookings.stream()
                .sorted(Comparator.comparing(booking -> booking.bookingRange().beginningBookingDate()))
                .map(Booking::toString)
                .toList();
    }

    /**
     * Retrieves all bookings sorted by space.
     *
     * @return a list of formatted booking strings
     */
    public List<String> getAllBookingsSortedBySpace() {
        List<Booking> bookings = bookingDAO.getAll();
        return bookings.stream()
                .sorted(Comparator.comparing(Booking::nameOfBookingSpace))
                .map(Booking::toString)
                .toList();
    }

    /**
     * Deletes a booking by its index in the user's booking list.
     *
     * @param loginOfUser the login of the user
     * @param indexOfBookingInList the index of the booking in the list
     */
    public void deleteBookingByIndex(String loginOfUser, int indexOfBookingInList) {
        bookingDAO.delete(bookingDAO.getAllForUser(loginOfUser).get(indexOfBookingInList).id());
    }
}