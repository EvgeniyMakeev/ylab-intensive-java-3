package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.WorkingHours;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("SpaceService Test")
@ExtendWith(MockitoExtension.class)
class SpaceServiceTest {

    @Mock
    private SpaceDAO spaceDAO;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private SpaceService spaceService;

    @Mock
    private Space mockSpace;

    @Test
    @DisplayName("SpaceService test: Add and Update Space - Should add or update space successfully")
    void addAndUpdateSpace_shouldAddOrUpdateSpace() {
        spaceService.addAndUpdateSpace("TestSpace", 8, 10, 5);

        verify(spaceDAO, times(1)).add(any(Space.class));
    }

    @Test
    @DisplayName("SpaceService test: Get Spaces - Should return all spaces")
    void getSpaces_shouldReturnAllSpaces() {
        List<Space> spaces = List.of(mockSpace);
        when(spaceDAO.getSpaces()).thenReturn(spaces);

        List<Space> result = spaceService.getSpaces();

        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(spaces);
    }

    @Test
    @DisplayName("SpaceService test: Delete Space - Should delete space and its bookings")
    void deleteSpace_shouldDeleteSpaceAndBookings() {
        doNothing().when(bookingService).deleteBookingsBySpace(anyString());

        spaceService.deleteSpace("TestSpace");

        verify(bookingService, times(1)).deleteBookingsBySpace("TestSpace");
        verify(spaceDAO, times(1)).delete("TestSpace");
    }

    @Test
    @DisplayName("SpaceService test: Initialize Free Slots for Booking - Should initialize free slots correctly")
    void initFreeSlotsForBooking_shouldInitializeFreeSlotsCorrectly() {
        WorkingHours workingHours = new WorkingHours(8, 10);
        int numberOfDays = 5;

        Map<LocalDate, Map<Integer, Boolean>> result = SpaceService.initFreeSlotsForBooking(workingHours, numberOfDays);

        assertThat(result).hasSize(numberOfDays);
        assertTrue(result.get(LocalDate.now()).get(8));
    }
}