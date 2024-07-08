package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.dto.SpaceAddDTO;
import dev.makeev.coworking_service_app.exceptions.SpaceAlreadyExistsException;
import dev.makeev.coworking_service_app.model.Space;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("SpaceService Test")
@ExtendWith(MockitoExtension.class)
class SpaceServiceTest {

    public static final String TEST_SPACE = "TestSpace";

    @Mock
    private SpaceDAO spaceDAO;

    @InjectMocks
    private SpaceService spaceService;

    @Mock
    private Space mockSpace;

    @Mock
    private SpaceAddDTO mockSpaceAddDTO;

    @Test
    @DisplayName("SpaceService test: Add Space - Should add or update space successfully")
    void addSpace_shouldAddNewSpace() throws SpaceAlreadyExistsException {
        spaceService.addSpace(mockSpaceAddDTO);

        verify(spaceDAO, times(1)).add(any(Space.class));
    }

    @Test
    @DisplayName("SpaceService test: Add Space which already exists - Should throw SpaceAlreadyExistsException")
    void addSpaceWhichAlreadyExists_shouldGetException() {
        when(mockSpaceAddDTO.name()).thenReturn(TEST_SPACE);
        when(spaceDAO.getSpaceByName(TEST_SPACE)).thenReturn(Optional.of(mockSpace));

        assertThatThrownBy(() ->
                spaceService.addSpace(mockSpaceAddDTO))
                .isInstanceOf(SpaceAlreadyExistsException.class);

        verify(spaceDAO, times(0)).add(any(Space.class));
    }

    @Test
    @DisplayName("SpaceService test: Get Spaces - Should return all spaces name")
    void getNamesOfSpaces_shouldReturnAllNamesOfSpaces() {
        List<String> spaces = List.of(mockSpace.toString());
        when(spaceDAO.getNamesOfSpaces()).thenReturn(spaces);

        List<String> result = spaceService.getNamesOfSpaces();

        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(spaces);
        verify(spaceDAO, times(1)).getNamesOfSpaces();
    }


    @Test
    @DisplayName("SpaceService test: Delete Space - Should delete space successfully")
    void deleteSpace_shouldDeleteSpace() {
        when(spaceDAO.getSpaceByName(TEST_SPACE)).thenReturn(Optional.of(mockSpace));

        spaceService.deleteSpace(TEST_SPACE);

        verify(spaceDAO, times(1)).delete(any(String.class));
    }
}