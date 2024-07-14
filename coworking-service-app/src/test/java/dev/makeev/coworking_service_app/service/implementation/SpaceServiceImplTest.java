package dev.makeev.coworking_service_app.service.implementation;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.dto.SpaceAddDTO;
import dev.makeev.coworking_service_app.dto.SpaceDTO;
import dev.makeev.coworking_service_app.exceptions.SpaceAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;
import dev.makeev.coworking_service_app.model.SlotsAvailableForBooking;
import dev.makeev.coworking_service_app.model.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("SpaceService Test")
@ExtendWith(MockitoExtension.class)
class SpaceServiceImplTest {

    public static final String TEST_SPACE = "TestSpace";

    @Mock
    private SpaceDAO spaceDAO;

    @InjectMocks
    private SpaceServiceImpl spaceServiceImpl;

    @Mock
    private Space mockSpace;

    @Mock
    private SpaceAddDTO mockSpaceAddDTO;

    @BeforeEach
    void setUp() {
        spaceServiceImpl = new SpaceServiceImpl(spaceDAO);
    }

    @Test
    @DisplayName("SpaceService test: Add Space - Should add new space successfully")
    void addSpace_shouldAddNewSpace() throws SpaceAlreadyExistsException, SpaceNotFoundException {
        when(mockSpaceAddDTO.name()).thenReturn(TEST_SPACE);
        when(mockSpaceAddDTO.hourOfBeginningWorkingDay()).thenReturn(8);
        when(mockSpaceAddDTO.hourOfEndingWorkingDay()).thenReturn(18);
        when(mockSpaceAddDTO.numberOfDaysAvailableForBooking()).thenReturn(7);

        spaceServiceImpl.addSpace(mockSpaceAddDTO);

        verify(spaceDAO, times(1)).add(any(Space.class));
    }

    @Test
    @DisplayName("SpaceService test: Add Space which already exists - Should throw SpaceAlreadyExistsException")
    void addSpaceWhichAlreadyExists_shouldThrowException() {
        when(mockSpaceAddDTO.name()).thenReturn(TEST_SPACE);
        when(spaceDAO.getSpaceByName(TEST_SPACE)).thenReturn(Optional.of(mockSpace));

        assertThatThrownBy(() -> spaceServiceImpl.addSpace(mockSpaceAddDTO))
                .isInstanceOf(SpaceAlreadyExistsException.class);

        verify(spaceDAO, never()).add(any(Space.class));
    }

    @Test
    @DisplayName("SpaceService test: Get Spaces - Should return all spaces")
    void getSpaces_shouldReturnAllSpaces() {
        List<String> spaceNames = List.of(TEST_SPACE);

        List<String> slots = new ArrayList<>();
        List<SlotsAvailableForBooking> availableSlots = List.of(new SlotsAvailableForBooking(LocalDate.now().toString(), slots));
        SpaceDTO expectedSpaceDTO = new SpaceDTO(TEST_SPACE, availableSlots);
        List<SpaceDTO> expectedSpaces = List.of(expectedSpaceDTO);

        when(spaceDAO.getNamesOfSpaces()).thenReturn(spaceNames);
        when(spaceDAO.getSpaceByName(TEST_SPACE)).thenReturn(Optional.of(mockSpace));

        Map<LocalDate, Map<Integer, Long>> bookingSlots = new HashMap<>();
        bookingSlots.put(LocalDate.now(), new HashMap<>());
        when(mockSpace.bookingSlots()).thenReturn(bookingSlots);

        List<SpaceDTO> result = spaceServiceImpl.getSpaces();

        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(expectedSpaces);
    }


    @Test
    @DisplayName("SpaceService test: Delete Space - Should delete space successfully")
    void deleteSpace_shouldDeleteSpace() throws SpaceNotFoundException {
        when(spaceDAO.getSpaceByName(TEST_SPACE)).thenReturn(Optional.of(mockSpace));

        spaceServiceImpl.deleteSpace(TEST_SPACE);

        verify(spaceDAO, times(1)).delete(TEST_SPACE);
    }

    @Test
    @DisplayName("SpaceService test: Delete Space which does not exist - Should throw SpaceNotFoundException")
    void deleteSpaceWhichDoesNotExist_shouldThrowException() {
        when(spaceDAO.getSpaceByName(TEST_SPACE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> spaceServiceImpl.deleteSpace(TEST_SPACE))
                .isInstanceOf(SpaceNotFoundException.class);

        verify(spaceDAO, never()).delete(TEST_SPACE);
    }
}
