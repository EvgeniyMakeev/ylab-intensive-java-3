package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.dto.SpaceAddDTO;
import dev.makeev.coworking_service_app.exceptions.SpaceAlreadyExistsException;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.service.implementation.SpaceServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

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

    @Test
    @DisplayName("SpaceService test: Add Space - Should add or update space successfully")
    void addSpace_shouldAddNewSpace() throws SpaceAlreadyExistsException {
        spaceServiceImpl.addSpace(mockSpaceAddDTO);

        verify(spaceDAO, times(1)).add(any(Space.class));
    }

    @Test
    @DisplayName("SpaceService test: Add Space which already exists - Should throw SpaceAlreadyExistsException")
    void addSpaceWhichAlreadyExists_shouldGetException() {
        when(mockSpaceAddDTO.name()).thenReturn(TEST_SPACE);
        when(spaceDAO.getSpaceByName(TEST_SPACE)).thenReturn(Optional.of(mockSpace));

        assertThatThrownBy(() ->
                spaceServiceImpl.addSpace(mockSpaceAddDTO))
                .isInstanceOf(SpaceAlreadyExistsException.class);

        verify(spaceDAO, times(0)).add(any(Space.class));
    }

//    @Test
//    @DisplayName("SpaceService test: Get Spaces - Should return all spaces name")
//    void getNamesOfSpaces_shouldReturnAllSpaces() {
//        List<String> spaces = List.of(mockSpace.toString());
//        when(spaceDAO.getNamesOfSpaces()).thenReturn(spaces);
//
//        List<String> result = spaceServiceImpl.getSpaces();
//
//        assertThat(result).isNotEmpty();
//        assertThat(result).isEqualTo(spaces);
//        verify(spaceDAO, times(1)).getNamesOfSpaces();
//    }


    @Test
    @DisplayName("SpaceService test: Delete Space - Should delete space successfully")
    void deleteSpace_shouldDeleteSpace() {
        when(spaceDAO.getSpaceByName(TEST_SPACE)).thenReturn(Optional.of(mockSpace));

        spaceServiceImpl.deleteSpace(TEST_SPACE);

        verify(spaceDAO, times(1)).delete(any(String.class));
    }
}