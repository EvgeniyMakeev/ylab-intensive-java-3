package dev.makeev.coworking_service_app.service.implementation;

import dev.makeev.coworking_service_app.advice.annotations.LoggingToDb;
import dev.makeev.coworking_service_app.dao.BookingDAO;
import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.dto.BookingAddDTO;
import dev.makeev.coworking_service_app.dto.BookingDTO;
import dev.makeev.coworking_service_app.exceptions.BookingNotFoundException;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailableException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;
import dev.makeev.coworking_service_app.mappers.BookingMapper;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.BookingRange;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.WorkingHours;
import dev.makeev.coworking_service_app.service.BookingService;
import dev.makeev.logging_time_starter.advice.annotations.LoggingTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * The {@code BookingServiceImpl} class implements the {@link BookingService} interface.
 * It provides methods to manage Booking.
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingDAO bookingDAO;
    private final SpaceDAO spaceDAO;
    private final BookingMapper bookingMapper;

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @LoggingToDb
    @Override
    public void addBooking(String login, BookingAddDTO bookingAddDTO) throws SpaceIsNotAvailableException, SpaceNotFoundException {
        Space bookingSpace = spaceDAO.getSpaceByName(bookingAddDTO.nameOfBookingSpace()).orElseThrow(SpaceNotFoundException::new);
        Booking booking = bookingMapper.toBooking(login, bookingAddDTO);
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
     * {@inheritdoc}
     */
    @LoggingTime
    @LoggingToDb
    @Override
    public List<BookingDTO> getAllBookingsForUser(String login) {
        return bookingDAO.getAllForUser(login)
                .stream()
                .map(bookingMapper::toBookingDTO)
                .toList();
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public List<BookingDTO> getAllBookingsSortedByUser() {
        return bookingDAO.getAll()
                .stream()
                .sorted(Comparator.comparing(Booking::login))
                .map(bookingMapper::toBookingDTO)
                .toList();
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @LoggingToDb
    @Override
    public void deleteBookingById(String login, long id) throws BookingNotFoundException {
        bookingDAO.getBookingById(id).orElseThrow(BookingNotFoundException::new);
        bookingDAO.delete(id);
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @LoggingToDb
    @Override
    public void deleteBookingByIdByAdmin(String login, long id) throws BookingNotFoundException {
        Booking booking = bookingDAO.getBookingById(id).orElseThrow(BookingNotFoundException::new);

        if (!booking.login().equals(login)) {
            throw new BookingNotFoundException();
        }

        bookingDAO.delete(id);
    }
}