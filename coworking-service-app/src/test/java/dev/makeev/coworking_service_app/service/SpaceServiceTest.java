package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

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

    @Test
    @DisplayName("SpaceService test: Add Space - Should add or update space successfully")
    void addSpace_shouldAddNewSpace() throws SpaceAlreadyExistsException {
        spaceService.addSpace(TEST_SPACE, 8, 10, 5);

        verify(spaceDAO, times(1)).add(any(Space.class));
    }

    @Test
    @DisplayName("SpaceService test: Add Space which already exists - Should throw SpaceAlreadyExistsException")
    void addSpaceWhichAlreadyExists_shouldGetException() {
        when(spaceDAO.getSpaceByName(TEST_SPACE)).thenReturn(Optional.of(mockSpace));

        assertThatThrownBy(() ->
                spaceService.addSpace(TEST_SPACE, 8, 10, 5))
                .isInstanceOf(SpaceAlreadyExistsException.class);

        verify(spaceDAO, times(0)).add(any(Space.class));
    }

    @Test
    @DisplayName("SpaceService test: Get Spaces - Should return all spaces name")
    void getNamesOfSpaces_shouldReturnAllNamesOfSpaces() {
        List<String> spaces = List.of(mockSpace.toString());
        when(spaceDAO.getNamesOfSpaces()).thenReturn(spaces);

        List<String> result = spaceService.getSpaces();

        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(spaces);
        verify(spaceDAO, times(1)).getNamesOfSpaces();
    }

    @Test
    @DisplayName("SpaceService test: Get Space by name - Should return space")
    void getSpaceByName_shouldReturnSpaceByName() {
        when(spaceDAO.getSpaceByName(TEST_SPACE)).thenReturn(Optional.of(mockSpace));

        Optional<Space> resultSpace = spaceDAO.getSpaceByName(TEST_SPACE);

        assertTrue(resultSpace.isPresent());
        assertThat(resultSpace).isEqualTo(Optional.of(mockSpace));
        verify(spaceDAO, times(1)).getSpaceByName(any(String.class));
    }

    @Test
    @DisplayName("SpaceService test: Delete Space - Should delete space successfully")
    void deleteSpace_shouldDeleteSpace() {
        spaceService.deleteSpace(TEST_SPACE);

        verify(spaceDAO, times(1)).delete(any(String.class));
    }
}