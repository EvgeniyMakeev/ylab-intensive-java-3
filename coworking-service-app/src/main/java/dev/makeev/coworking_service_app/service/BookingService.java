package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.BookingDAO;
import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailableException;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.BookingRange;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.UserBooking;
import dev.makeev.coworking_service_app.model.WorkingHours;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Service class for managing bookings.
 */
public class BookingService {

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
     * @param dateBookingFrom the start date of the booking
     * @param hourBookingFrom the start hour of the booking
     * @param dateBookingTo the end date of the booking
     * @param hourBookingTo the end hour of the booking
     * @throws SpaceIsNotAvailableException if the space is not available for the specified date and time
     */
    public void addBooking(String loginOfUser, String bookingSpaceName, LocalDate dateBookingFrom, int hourBookingFrom,
                           LocalDate dateBookingTo, int hourBookingTo) throws SpaceIsNotAvailableException {

        Space bookingSpace = spaceDAO.getSpaceByName(bookingSpaceName);
        BookingRange bookingRange = new BookingRange(dateBookingFrom, hourBookingFrom, dateBookingTo, hourBookingTo);

        if (isSpaceAvailableForBookingOnDateAndTime(bookingSpace, bookingRange, bookingSpace.workingHours())) {
            bookingDAO.add(loginOfUser, new Booking(bookingSpace, bookingRange));

            Space space = spaceDAO.getSpaceByName(bookingSpace.name());

            Map<LocalDate, Map<Integer, Boolean>> updatedSlots = space.bookingSlots();
            updateBookingSlots(updatedSlots, bookingRange, space.workingHours(), false);

            spaceDAO.add(new Space(bookingSpace.name(), space.workingHours(), updatedSlots));
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

        if (isValidDateAndTimeOfBooking(bookingSpace, bookingRange, workingHours)) {
            return false;
        }

        return bookingRange.dateBookingFrom().datesUntil(bookingRange.dateBookingTo().plusDays(1))
                .allMatch(date -> {
                    Map<Integer, Boolean> slots = bookingSpace.bookingSlots().get(date);
                    int startHour = (date.equals(bookingRange.dateBookingFrom())) ?
                            bookingRange.hourBookingFrom() : workingHours.hourOfStartWorkingDay();
                    int endHour = (date.equals(bookingRange.dateBookingTo())) ?
                            bookingRange.hourBookingTo() : workingHours.hourOfEndWorkingDay();

                    return slots.entrySet().stream()
                            .filter(entry -> entry.getKey() >= startHour && entry.getKey() < endHour)
                            .allMatch(Map.Entry::getValue);
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
        return bookingRange.dateBookingFrom().isBefore(LocalDate.now()) &&
                (!bookingSpace.bookingSlots().containsKey(bookingRange.dateBookingFrom()) ||
                        !bookingSpace.bookingSlots().containsKey(bookingRange.dateBookingTo())) &&
                (bookingRange.hourBookingFrom() < workingHours.hourOfStartWorkingDay() ||
                        bookingRange.hourBookingTo() > workingHours.hourOfEndWorkingDay());
    }

    /**
     * Updates the booking slots for a space.
     *
     * @param bookingSlots the booking slots to update
     * @param bookingRange the range of the booking
     * @param workingHours the working hours of the space
     * @param available the availability status to set
     */
    private void updateBookingSlots(Map<LocalDate, Map<Integer, Boolean>> bookingSlots,
                                    BookingRange bookingRange, WorkingHours workingHours, Boolean available) {
        bookingRange.dateBookingFrom().datesUntil(bookingRange.dateBookingTo().plusDays(1))
                .forEach(date -> {
                    Map<Integer, Boolean> slots = bookingSlots.get(date);
                    int startHour = (date.equals(bookingRange.dateBookingFrom())) ?
                            bookingRange.hourBookingFrom() : workingHours.hourOfStartWorkingDay();
                    int endHour = (date.equals(bookingRange.dateBookingTo())) ?
                            bookingRange.hourBookingTo() : workingHours.hourOfEndWorkingDay();

                    IntStream.range(startHour, endHour).forEach(hour -> slots.put(hour, available));
                });
    }

    /**
     * Retrieves all bookings for a user.
     *
     * @param loginOfUser the login of the user
     * @return a list of formatted booking strings
     */
    public List<String> getAllBookingsForUser(String loginOfUser) {
        List<UserBooking> bookings = bookingDAO.getAllForUser(loginOfUser);
        List<String> formatedBookings = new ArrayList<>();
        for (UserBooking userBooking : bookings) {
            formatedBookings.add(String.format("%d. Space: %s | From: %02d:00 %s | To: %02d:00 %s\n",
                    bookings.indexOf(userBooking) + 1, userBooking.booking().bookingSpace().name(),
                    userBooking.booking().bookingRange().hourBookingFrom(), userBooking.booking().bookingRange().dateBookingFrom(),
                    userBooking.booking().bookingRange().hourBookingTo(), userBooking.booking().bookingRange().dateBookingTo()));
        }
        return formatedBookings;
    }

    /**
     * Retrieves all bookings sorted by user.
     *
     * @return a list of formatted booking strings
     */
    public List<String> getAllBookingsSortedByUser() {
        Map<String, List<UserBooking>> allBookings = bookingDAO.getAll();

        return allBookings.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(String::compareToIgnoreCase))
                .flatMap(entry -> entry.getValue().stream())
                .map(UserBooking::format)
                .toList();
    }

    /**
     * Retrieves all bookings sorted by date.
     *
     * @return a list of formatted booking strings
     */
    public List<String> getAllBookingsSortedByDate() {
        Map<String, List<UserBooking>> allBookings = bookingDAO.getAll();

        return allBookings.values().stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(userBooking -> userBooking.booking().bookingRange().dateBookingFrom()))
                .map(UserBooking::format)
                .toList();
    }

    /**
     * Retrieves all bookings sorted by space.
     *
     * @return a list of formatted booking strings
     */
    public List<String> getAllBookingsSortedBySpace() {
        Map<String, List<UserBooking>> allBookings = bookingDAO.getAll();

        return allBookings.values().stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(userBooking -> userBooking.booking().bookingSpace().name()))
                .map(UserBooking::format)
                .toList();
    }

    /**
     * Deletes a booking by its index in the user's booking list.
     *
     * @param loginOfUser the login of the user
     * @param indexOfBookingInList the index of the booking in the list
     */
    public void deleteBookingByIndex(String loginOfUser, int indexOfBookingInList) {
        deleteBooking(bookingDAO.getAllForUser(loginOfUser).get(indexOfBookingInList));
    }

    /**
     * Deletes all bookings for a specified space.
     *
     * @param spaceName the name of the space
     */
    public void deleteBookingsBySpace(String spaceName) {
        List<UserBooking> allBookings = new ArrayList<>();
        bookingDAO.getAll().values().forEach(allBookings::addAll);

        allBookings.stream()
                .filter(userBooking -> userBooking.booking().bookingSpace().name().equalsIgnoreCase(spaceName))
                .forEach(this::deleteBooking);


    }

    /**
     * Deletes a booking.
     *
     * @param bookingForDelete the booking to delete
     */
    private void deleteBooking(UserBooking bookingForDelete) {
        bookingDAO.delete(bookingForDelete.userLogin(), bookingForDelete.booking().id());

        Map<LocalDate, Map<Integer, Boolean>> updatedSlots =
                spaceDAO.getSpaceByName(bookingForDelete.booking().bookingSpace().name()).bookingSlots();
        updateBookingSlots(updatedSlots, bookingForDelete.booking().bookingRange(),
                bookingForDelete.booking().bookingSpace().workingHours(), true);

        spaceDAO.add(new Space(bookingForDelete.booking().bookingSpace().name(),
                bookingForDelete.booking().bookingSpace().workingHours(), updatedSlots));
    }
}