package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.BookingDAO;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.BookingRange;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.UserBooking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("BookingService Test")
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

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

        when(mockSpace.name()).thenReturn("TestSpace");
        when(mockBooking.bookingSpace()).thenReturn(mockSpace);
        when(mockBooking.bookingRange()).thenReturn(mockBookingRange);
    }

    @Test
    @DisplayName("BookingService test: Get All Bookings Sorted By User - Should return all bookings sorted by user")
    void getAllBookingsSortedByUser_shouldReturnAllBookingsSortedByUser() {
        Map<String, List<UserBooking>> allBookings = new HashMap<>();
        List<UserBooking> bookings = new ArrayList<>();
        bookings.add(new UserBooking("TestUser", mockBooking));
        allBookings.put("TestUser", bookings);
        when(bookingDAO.getAll()).thenReturn(allBookings);

        List<String> result = bookingService.getAllBookingsSortedByUser();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).contains("TestUser");
    }

    @Test
    @DisplayName("BookingService test: Get All Bookings Sorted By Date - Should return all bookings sorted by date")
    void getAllBookingsSortedByDate_shouldReturnAllBookingsSortedByDate() {
        Map<String, List<UserBooking>> allBookings = new HashMap<>();
        List<UserBooking> bookings = new ArrayList<>();
        bookings.add(new UserBooking("TestUser", mockBooking));
        allBookings.put("TestUser", bookings);
        when(bookingDAO.getAll()).thenReturn(allBookings);

        List<String> result = bookingService.getAllBookingsSortedByDate();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).contains("TestSpace");
    }

    @Test
    @DisplayName("BookingService test: Get All Bookings Sorted By Space - Should return all bookings sorted by space")
    void getAllBookingsSortedBySpace_shouldReturnAllBookingsSortedBySpace() {
        Map<String, List<UserBooking>> allBookings = new HashMap<>();
        List<UserBooking> bookings = new ArrayList<>();
        bookings.add(new UserBooking("TestUser", mockBooking));
        allBookings.put("TestUser", bookings);
        when(bookingDAO.getAll()).thenReturn(allBookings);

        List<String> result = bookingService.getAllBookingsSortedBySpace();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).contains("TestSpace");
    }

}