package dev.makeev.coworking_service_app.service.implementation;

import dev.makeev.coworking_service_app.dao.BookingDAO;
import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.dto.BookingAddDTO;
import dev.makeev.coworking_service_app.dto.BookingDTO;
import dev.makeev.coworking_service_app.exceptions.BookingNotFoundException;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailableException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;
import dev.makeev.coworking_service_app.mappers.BookingMapper;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.WorkingHours;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("BookingService Test")
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private static final String NAME_OF_SPACE = "TestSpace";
    private static final String LOGIN = "TestLogin";

    @Mock
    private BookingDAO bookingDAO;

    @Mock
    private SpaceDAO spaceDAO;

    @InjectMocks
    private BookingServiceImpl bookingServiceImpl;

    @Mock
    private Space mockSpace;

    @Mock
    private BookingAddDTO mockBookingAddDTO;

    @Mock
    private Booking mockBooking1;

    @Mock
    private Booking mockBooking2;

    @Mock
    private WorkingHours mockWorkingHours;

    @BeforeEach
    void setUp() {
        bookingServiceImpl = new BookingServiceImpl(bookingDAO, spaceDAO, Mappers.getMapper(BookingMapper.class));
    }

    @Test
    @DisplayName("BookingService test: Add Booking - Should add booking if space is available")
    void addBooking_shouldAddBookingIfSpaceIsAvailable() throws SpaceIsNotAvailableException, SpaceNotFoundException {
        when(spaceDAO.getSpaceByName(anyString())).thenReturn(Optional.of(mockSpace));
        when(mockSpace.bookingSlots()).thenReturn(Map.of(LocalDate.of(2024, 8, 15), Map.of(10, 0L, 11, 0L)));
        when(mockSpace.workingHours()).thenReturn(mockWorkingHours);
        when(mockWorkingHours.hourOfBeginningWorkingDay()).thenReturn(8);
        when(mockWorkingHours.hourOfEndingWorkingDay()).thenReturn(18);
        when(mockBookingAddDTO.nameOfBookingSpace()).thenReturn(NAME_OF_SPACE);
        when(mockBookingAddDTO.beginningBookingHour()).thenReturn(10);
        when(mockBookingAddDTO.beginningBookingDate()).thenReturn("2024-08-15");
        when(mockBookingAddDTO.endingBookingHour()).thenReturn(11);
        when(mockBookingAddDTO.endingBookingDate()).thenReturn("2024-08-15");

        bookingServiceImpl.addBooking(LOGIN, mockBookingAddDTO);

        verify(bookingDAO, times(1)).add(any(Booking.class));
    }

    @Test
    @DisplayName("BookingService test: Add Booking - Should throw exception if space is not available")
    void addBooking_shouldThrowExceptionIfSpaceIsNotAvailable() {
        when(spaceDAO.getSpaceByName(anyString())).thenReturn(Optional.of(mockSpace));
        when(mockSpace.workingHours()).thenReturn(mockWorkingHours);
        when(mockBookingAddDTO.nameOfBookingSpace()).thenReturn(NAME_OF_SPACE);
        when(mockWorkingHours.hourOfBeginningWorkingDay()).thenReturn(8);
        when(mockBookingAddDTO.beginningBookingDate()).thenReturn("2024-08-15");
        when(mockBookingAddDTO.endingBookingDate()).thenReturn("2024-08-15");

        assertThatThrownBy(() -> bookingServiceImpl.addBooking(LOGIN, mockBookingAddDTO))
                .isInstanceOf(SpaceIsNotAvailableException.class);

        verify(bookingDAO, never()).add(any(Booking.class));
    }

    @Test
    @DisplayName("BookingService test: Add Booking - Should throw exception if space is not found")
    void addBooking_shouldThrowExceptionIfSpaceIsNotFound() {
        when(spaceDAO.getSpaceByName(anyString())).thenReturn(Optional.empty());
        when(mockBookingAddDTO.nameOfBookingSpace()).thenReturn(NAME_OF_SPACE);

        assertThatThrownBy(() -> bookingServiceImpl.addBooking(LOGIN, mockBookingAddDTO))
                .isInstanceOf(SpaceNotFoundException.class);

        verify(bookingDAO, never()).add(mockBooking1);
    }

    @Test
    @DisplayName("BookingService test: Get All Bookings For User - Should return all bookings for user")
    void getAllBookingsForUser_shouldReturnAllBookingsForUser() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(mockBooking1);
        bookings.add(mockBooking2);
        when(bookingDAO.getAllForUser(LOGIN)).thenReturn(bookings);

        List<BookingDTO> result = bookingServiceImpl.getAllBookingsForUser(LOGIN);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        verify(bookingDAO, times(1)).getAllForUser(LOGIN);
    }

    @Test
    @DisplayName("BookingService test: Get All Bookings Sorted By User - Should return all bookings sorted by user")
    void getAllBookingsSortedByUser_shouldReturnAllBookingsSortedByUser() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(mockBooking1);
        when(mockBooking1.login()).thenReturn(LOGIN);
        bookings.add(mockBooking2);
        when(mockBooking2.login()).thenReturn("LOGIN-2");
        when(bookingDAO.getAll()).thenReturn(bookings);

        List<BookingDTO> result = bookingServiceImpl.getAllBookingsSortedByUser();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        verify(bookingDAO, times(1)).getAll();
    }

    @Test
    @DisplayName("BookingService test: Delete Booking By Id - Should delete booking if it exists")
    void deleteBookingById_shouldDeleteBookingIfItExists() throws BookingNotFoundException {
        when(bookingDAO.getBookingById(1L)).thenReturn(Optional.of(mockBooking1));

        bookingServiceImpl.deleteBookingById(LOGIN, 1L);

        verify(bookingDAO, times(1)).delete(1L);
    }

    @Test
    @DisplayName("BookingService test: Delete Booking By Id - Should throw exception if booking does not exist")
    void deleteBookingById_shouldThrowExceptionIfBookingDoesNotExist() {
        when(bookingDAO.getBookingById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingServiceImpl.deleteBookingById(LOGIN, 1L))
                .isInstanceOf(BookingNotFoundException.class);

        verify(bookingDAO, never()).delete(1L);
    }
}
