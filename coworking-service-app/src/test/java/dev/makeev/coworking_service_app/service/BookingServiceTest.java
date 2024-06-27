package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.BookingDAO;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.BookingRange;
import dev.makeev.coworking_service_app.model.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("BookingService Test")
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private final static String SPACE = "TestSpace";

    @Mock
    private BookingDAO bookingDAO;

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private Space mockSpace;

    @Mock
    private Booking mockBooking;

    @Mock
    private BookingRange mockBookingRange;

    @BeforeEach
    void setUp() {
        when(mockSpace.name()).thenReturn(SPACE);
        when(mockBooking.nameOfBookingSpace()).thenReturn("Test Space");
        when(mockBooking.bookingRange()).thenReturn(mockBookingRange);
    }

    @Test
    @DisplayName("BookingService test: Get All Bookings Sorted By User - Should return all bookings sorted by user")
    void getAllBookingsSortedByUser_shouldReturnAllBookingsSortedByUser() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(mockBooking);
        when(bookingDAO.getAll()).thenReturn(bookings);

        List<String> result = bookingService.getAllBookingsSortedByUser();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).contains("Test Space");
        verify(bookingDAO, times(1)).getAll();
    }

    @Test
    @DisplayName("BookingService test: Get All Bookings Sorted By Date - Should return all bookings sorted by date")
    void getAllBookingsSortedByDate_shouldReturnAllBookingsSortedByDate() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(mockBooking);
        when(bookingDAO.getAll()).thenReturn(bookings);

        List<String> result = bookingService.getAllBookingsSortedByDate();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).contains(mockBooking.nameOfBookingSpace());
        verify(bookingDAO, times(1)).getAll();
    }

    @Test
    @DisplayName("BookingService test: Get All Bookings Sorted By Space - Should return all bookings sorted by space")
    void getAllBookingsSortedBySpace_shouldReturnAllBookingsSortedBySpace() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(mockBooking);
        when(bookingDAO.getAll()).thenReturn(bookings);

        List<String> result = bookingService.getAllBookingsSortedBySpace();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).contains("Test Space");
        verify(bookingDAO, times(1)).getAll();
    }
}