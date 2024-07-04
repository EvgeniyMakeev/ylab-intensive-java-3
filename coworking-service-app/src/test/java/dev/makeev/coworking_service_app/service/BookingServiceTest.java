package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.BookingDAO;
import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.BookingRange;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.WorkingHours;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("BookingService Test")
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingDAO bookingDAO;

    @Mock
    private SpaceDAO spaceDAO;

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private Space mockSpace;

    @Mock
    private Booking mockBooking;

    @Mock
    private BookingRange mockBookingRange;

    @Mock
    private WorkingHours mockWorkingHours;

//    @Test
//    @DisplayName("BookingService test: Add Booking - Should add booking if space is available")
//    void addBooking_shouldAddBookingIfSpaceIsAvailable() throws SpaceIsNotAvailableException {
//        when(spaceDAO.getSpaceByName(anyString())).thenReturn(Optional.of(mockSpace));
//        when(mockSpace.bookingSlots()).thenReturn(Map.of(LocalDate.now(), Map.of(10, 0L, 11, 0L)));
//        when(mockSpace.workingHours()).thenReturn(mockWorkingHours);
//        when(mockWorkingHours.hourOfBeginningWorkingDay()).thenReturn(8);
//        when(mockWorkingHours.hourOfEndingWorkingDay()).thenReturn(18);
//
//
//        bookingService.addBooking("testUser", "Test Space", LocalDate.now(), 10, LocalDate.now(), 11);
//
//        verify(bookingDAO, times(1)).add(any(Booking.class));
//    }

//    @Test
//    @DisplayName("BookingService test: Add Booking - Should throw exception if space is not available")
//    void addBooking_shouldThrowExceptionIfSpaceIsNotAvailable() {
//        when(spaceDAO.getSpaceByName(anyString())).thenReturn(Optional.of(mockSpace));
//        when(mockSpace.bookingSlots()).thenReturn(Map.of(LocalDate.now(), Map.of(10, 1L, 11, 1L)));
//        when(mockSpace.workingHours()).thenReturn(mockWorkingHours);
//        when(mockWorkingHours.hourOfBeginningWorkingDay()).thenReturn(8);
//        when(mockWorkingHours.hourOfEndingWorkingDay()).thenReturn(18);
//
//        assertThatThrownBy(() -> bookingService.addBooking("testUser", "Test Space", LocalDate.now(), 10, LocalDate.now(), 11))
//                .isInstanceOf(SpaceIsNotAvailableException.class);
//
//        verify(bookingDAO, never()).add(any(Booking.class));
//    }

//    @Test
//    @DisplayName("BookingService test: Get All Bookings For User - Should return all bookings for user")
//    void getAllBookingsForUser_shouldReturnAllBookingsForUser() {
//        List<Booking> bookings = new ArrayList<>();
//        bookings.add(mockBooking);
//        when(mockBooking.bookingRange()).thenReturn(mockBookingRange);
//        when(bookingDAO.getAllForUser(anyString())).thenReturn(bookings);
//        when(mockBooking.toString()).thenReturn("1. Space: null | From: 00:00 null | To: 00:00 null");
//
//        List<String> result = bookingService.getAllBookingsForUser("testUser");
//
//        assertThat(result).isNotEmpty();
//        assertThat(result.get(0)).contains(mockBooking.toString());
//        verify(bookingDAO, times(1)).getAllForUser("testUser");
//    }

//    @Test
//    @DisplayName("BookingService test: Get All Bookings Sorted By User - Should return all bookings sorted by user")
//    void getAllBookingsSortedByUser_shouldReturnAllBookingsSortedByUser() {
//        List<Booking> bookings = new ArrayList<>();
//        bookings.add(mockBooking);
//        when(bookingDAO.getAll()).thenReturn(bookings);
//        when(mockBooking.toString()).thenReturn("1. Space: null | From: 00:00 null | To: 00:00 null");
//
//        List<String> result = bookingService.getAllBookingsSortedByUser();
//
//        assertThat(result).isNotEmpty();
//        assertThat(result.get(0)).contains(mockBooking.toString());
//        verify(bookingDAO, times(1)).getAll();
//    }


//    @Test
//    @DisplayName("BookingService test: Delete Booking By Index - Should delete booking by index")
//    void deleteBookingByIndex_shouldDeleteBookingByIndex() {
//        List<Booking> bookings = new ArrayList<>();
//        bookings.add(mockBooking);
//        when(bookingDAO.getAllForUser(anyString())).thenReturn(bookings);
//
//        bookingService.deleteBookingByIndex("testUser", 0);
//
//        verify(bookingDAO, times(1)).delete(anyLong());
//    }
}
