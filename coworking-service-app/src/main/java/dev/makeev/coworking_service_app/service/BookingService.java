package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.BookingDAO;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailablException;
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

public class BookingService {

    private final BookingDAO bookingDAO;
    private final SpaceDAO spaceDAO;

    public BookingService(BookingDAO bookingDAO, SpaceDAO spaceDAO) {
        this.bookingDAO = bookingDAO;
        this.spaceDAO = spaceDAO;
    }

    public void init() throws SpaceIsNotAvailablException {
        addBooking("User1", spaceDAO.getSpaceByName("Workplace No. 1"),
                LocalDate.now(), 8, LocalDate.now().plusDays(2), 15);
        addBooking("User1", spaceDAO.getSpaceByName("Workplace No. 1"),
                LocalDate.now().plusDays(5), 10, LocalDate.now().plusDays(5), 18);
        addBooking("User1", spaceDAO.getSpaceByName("Conference hall"),
                LocalDate.now().plusDays(1), 11, LocalDate.now().plusDays(4), 15);

        addBooking("User2", spaceDAO.getSpaceByName("Workplace No. 1"),
                LocalDate.now().plusDays(3), 11, LocalDate.now().plusDays(3), 14);
        addBooking("User2", spaceDAO.getSpaceByName("Workplace No. 1"),
                LocalDate.now().plusDays(11), 8, LocalDate.now().plusDays(13), 20);
        addBooking("User2", spaceDAO.getSpaceByName("Conference hall"),
                LocalDate.now().plusDays(8), 11, LocalDate.now().plusDays(12), 15);
    }

    public void addBooking(String loginOfUser, Space bookingSpace, LocalDate dateBookingFrom, int hourBookingFrom,
                           LocalDate dateBookingTo, int hourBookingTo) throws SpaceIsNotAvailablException {


        BookingRange bookingRange = new BookingRange(dateBookingFrom, hourBookingFrom, dateBookingTo, hourBookingTo);

        if (isSpaceAvailableForBookingOnDateAndTime(bookingSpace, bookingRange, bookingSpace.workingHours())) {
            bookingDAO.add(loginOfUser, new Booking(bookingSpace, bookingRange));

            Space space = spaceDAO.getSpaceByName(bookingSpace.name());

            Map<LocalDate, Map<Integer, Boolean>> updatedSlots = space.bookingSlots();
            updateBookingSlots(updatedSlots, bookingRange, space.workingHours(), false);

            spaceDAO.add(new Space(bookingSpace.name(), space.workingHours(), updatedSlots));
        } else {
            throw new SpaceIsNotAvailablException();
        }
    }

    private Boolean isSpaceAvailableForBookingOnDateAndTime(Space bookingSpace, BookingRange bookingRange, WorkingHours workingHours) {

        boolean isValidDateAndTimeOfBooking = bookingRange.dateBookingFrom().isBefore(LocalDate.now()) &&
                (!bookingSpace.bookingSlots().containsKey(bookingRange.dateBookingFrom()) ||
                        !bookingSpace.bookingSlots().containsKey(bookingRange.dateBookingTo())) &&
                (bookingRange.hourBookingFrom() < workingHours.hourOfStartWorkingDay() ||
                        bookingRange.hourBookingTo() > workingHours.hourOfEndWorkingDay());

        if (isValidDateAndTimeOfBooking) {
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

    public List<String> getAllBookingsSortedByUser() {
        Map<String, List<UserBooking>> allBookings = bookingDAO.getAll();

        return allBookings.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(String::compareToIgnoreCase))
                .flatMap(entry -> entry.getValue().stream())
                .map(UserBooking::format)
                .toList();
    }

    public List<String> getAllBookingsSortedByDate() {
        Map<String, List<UserBooking>> allBookings = bookingDAO.getAll();

        return allBookings.values().stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(userBooking -> userBooking.booking().bookingRange().dateBookingFrom()))
                .map(UserBooking::format)
                .toList();
    }

    public List<String> getAllBookingsSortedBySpace() {
        Map<String, List<UserBooking>> allBookings = bookingDAO.getAll();

        return allBookings.values().stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(userBooking -> userBooking.booking().bookingSpace().name()))
                .map(UserBooking::format)
                .toList();
    }

    public void deleteBookingByIndex(String loginOfUser, int indexOfBookingInList) {
        deleteBooking(bookingDAO.getAllForUser(loginOfUser).get(indexOfBookingInList));
    }

    public void deleteBookingsBySpace(String spaceName) {
        List<UserBooking> allBookings = new ArrayList<>();
        bookingDAO.getAll().values().forEach(allBookings::addAll);

        allBookings.stream()
                .filter(userBooking -> userBooking.booking().bookingSpace().name().equalsIgnoreCase(spaceName))
                .forEach(this::deleteBooking);
    }

    private void deleteBooking(UserBooking bookingForDelete) {
        bookingDAO.delete(bookingForDelete.userLogin(), bookingForDelete.booking().id());

        Map<LocalDate, Map<Integer, Boolean>> updatedSlots =
                spaceDAO.getSpaceByName(bookingForDelete.booking().bookingSpace().name()).bookingSlots();
        updateBookingSlots(updatedSlots, bookingForDelete.booking().bookingRange(),
                bookingForDelete.booking().bookingSpace().workingHours(),true);

        spaceDAO.add(new Space(bookingForDelete.booking().bookingSpace().name(),
                bookingForDelete.booking().bookingSpace().workingHours(), updatedSlots));
    }
}
