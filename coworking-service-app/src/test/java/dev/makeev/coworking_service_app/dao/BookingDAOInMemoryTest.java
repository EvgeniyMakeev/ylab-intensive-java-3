package dev.makeev.coworking_service_app.dao;


import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.UserBooking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("BookingDAOInMemory Test")
class BookingDAOInMemoryTest {

    private BookingDAO bookingDAO;
    private Booking mockBooking;
    private static final String USER_LOGIN = "TestUser";

    @BeforeEach
    void setUp() {
        bookingDAO = new BookingDAOInMemory();
        mockBooking = mock(Booking.class);

        when(mockBooking.id()).thenReturn(1L);
        Space mockSpace = mock(Space.class);
        when(mockSpace.name()).thenReturn("TestSpace");
        when(mockBooking.bookingSpace()).thenReturn(mockSpace);
    }

    @Test
    @DisplayName("BookingDAOInMemory test: Add Booking - Should add new booking for user")
    void add_shouldAddBooking() {
        bookingDAO.add(USER_LOGIN, mockBooking);

        List<UserBooking> bookings = bookingDAO.getAllForUser(USER_LOGIN);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).booking()).isEqualTo(mockBooking);
    }

    @Test
    @DisplayName("BookingDAOInMemory test: Get All Bookings for User - Should return all bookings for a user")
    void getAllForUser_shouldReturnAllBookingsForUser() {
        bookingDAO.add(USER_LOGIN, mockBooking);
        bookingDAO.add(USER_LOGIN, mockBooking);

        List<UserBooking> bookings = bookingDAO.getAllForUser(USER_LOGIN);
        assertThat(bookings).hasSize(2);
        assertThat(bookings).extracting("booking").containsOnly(mockBooking);
    }

    @Test
    @DisplayName("BookingDAOInMemory test: Get All Bookings - Should return all bookings")
    void getAll_shouldReturnAllBookings() {
        bookingDAO.add(USER_LOGIN, mockBooking);
        bookingDAO.add("AnotherUser", mockBooking);

        Map<String, List<UserBooking>> allBookings = bookingDAO.getAll();
        assertThat(allBookings).isNotNull();
        assertThat(allBookings.size()).isEqualTo(2);
        assertThat(allBookings).extractingByKey(USER_LOGIN).isNotNull();
        assertThat(allBookings).extractingByKey("AnotherUser").isNotNull();
    }

    @Test
    @DisplayName("BookingDAOInMemory test: Delete Booking - Should delete a booking for a user")
    void delete_shouldDeleteBooking() {
        bookingDAO.add(USER_LOGIN, mockBooking);
        bookingDAO.delete(USER_LOGIN, 1L);

        List<UserBooking> bookings = bookingDAO.getAllForUser(USER_LOGIN);
        assertThat(bookings).isEmpty();
    }

    @Test
    @DisplayName("BookingDAOInMemory test: Delete Booking - Should not delete a booking with incorrect ID")
    void delete_shouldNotDeleteBookingWithIncorrectId() {
        bookingDAO.add(USER_LOGIN, mockBooking);
        bookingDAO.delete(USER_LOGIN, 2L);

        List<UserBooking> bookings = bookingDAO.getAllForUser(USER_LOGIN);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).booking()).isEqualTo(mockBooking);
    }
}