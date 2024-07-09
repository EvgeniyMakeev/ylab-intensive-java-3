package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.BookingDAO;
import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailableException;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.BookingRange;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.WorkingHours;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("BookingService Test")
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private static final String NAME_OF_SPACE = "TestSpace";
    private static final String LOGIN = "TestLogin";

    @Mock
    private BookingDAO bookingDAO;

    @Mock
    private SpaceDAO spaceDAO;

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private Space mockSpace;

    @Mock
    private Booking mockBooking1;

    @Mock
    private Booking mockBooking2;

    @Mock
    private WorkingHours mockWorkingHours;

    @Test
    @DisplayName("BookingService test: Add Booking - Should add booking if space is available")
    void addBooking_shouldAddBookingIfSpaceIsAvailable() throws SpaceIsNotAvailableException {
        when(spaceDAO.getSpaceByName(anyString())).thenReturn(Optional.of(mockSpace));
        when(mockSpace.bookingSlots()).thenReturn(Map.of(LocalDate.now(), Map.of(10, 0L, 11, 0L)));
        when(mockSpace.workingHours()).thenReturn(mockWorkingHours);
        when(mockWorkingHours.hourOfBeginningWorkingDay()).thenReturn(8);
        when(mockWorkingHours.hourOfEndingWorkingDay()).thenReturn(18);


        Booking booking = new Booking(LOGIN, NAME_OF_SPACE,
                new BookingRange(LocalDate.now(), 10, LocalDate.now(), 11));

        bookingService.addBooking(LOGIN, booking);

        verify(bookingDAO, times(1)).add(any(Booking.class));
    }

    @Test
    @DisplayName("BookingService test: Add Booking - Should throw exception if space is not available")
    void addBooking_shouldThrowExceptionIfSpaceIsNotAvailable() {
        when(spaceDAO.getSpaceByName(anyString())).thenReturn(Optional.of(mockSpace));
        when(mockSpace.bookingSlots()).thenReturn(Map.of(LocalDate.now(), Map.of(10, 1L, 11, 1L)));
        when(mockSpace.workingHours()).thenReturn(mockWorkingHours);
        when(mockWorkingHours.hourOfBeginningWorkingDay()).thenReturn(8);
        when(mockWorkingHours.hourOfEndingWorkingDay()).thenReturn(18);

        Booking booking = new Booking(LOGIN, NAME_OF_SPACE,
                new BookingRange(LocalDate.now(), 10, LocalDate.now(), 11));

        assertThatThrownBy(() -> bookingService.addBooking("testUser", booking))
                .isInstanceOf(SpaceIsNotAvailableException.class);

        verify(bookingDAO, never()).add(any(Booking.class));
    }

    @Test
    @DisplayName("BookingService test: Get All Bookings For User - Should return all bookings for user")
    void getAllBookingsForUser_shouldReturnAllBookingsForUser() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(mockBooking1);
        bookings.add(mockBooking1);
        when(bookingDAO.getAllForUser(LOGIN)).thenReturn(bookings);

        List<Booking> result = bookingService.getAllBookingsForUser(LOGIN);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        verify(bookingDAO, times(1)).getAllForUser(LOGIN);
    }

    @Test
    @DisplayName("BookingService test: Get All Bookings Sorted By User - Should return all bookings sorted by user")
    void getAllBookingsSortedByUser_shouldReturnAllBookingsSortedByUser() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(mockBooking1);
        when(mockBooking1.loginOfUser()).thenReturn(LOGIN);
        bookings.add(mockBooking2);
        when(mockBooking2.loginOfUser()).thenReturn("LOGIN-2");
        when(bookingDAO.getAll()).thenReturn(bookings);

        List<Booking> result = bookingService.getAllBookingsSortedByUser();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        verify(bookingDAO, times(1)).getAll();
    }

}
