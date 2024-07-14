package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.BookingDAO;
import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.dto.BookingAddDTO;
import dev.makeev.coworking_service_app.exceptions.BookingNotFoundException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;
import dev.makeev.coworking_service_app.mappers.BookingMapper;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.service.implementation.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
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
    private BookingAddDTO mockBookingAddDTO;

    @Mock
    private Booking mockBooking1;


    @BeforeEach
    void setUp() {
        BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
        bookingServiceImpl = new BookingServiceImpl(bookingDAO, spaceDAO, bookingMapper);
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
