package dev.makeev.coworking_service_app.dao.impl;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.model.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SpaceDAOInMemory Test")
class SpaceDAOInMemoryTest {

    private SpaceDAO spaceDAO;
    private Space mockSpace;

    @BeforeEach
    void setUp() {
        spaceDAO = new SpaceDAOInMemory();
        mockSpace = Mockito.mock(Space.class);

        Mockito.when(mockSpace.name()).thenReturn("TestSpace");
    }

    @Test
    @DisplayName("SpaceDAOInMemory test: Add and Update Space - Should add new space")
    void add_shouldAddSpace() {
        spaceDAO.add(mockSpace);

        Space retrievedSpace = spaceDAO.getSpaceByName("TestSpace");
        assertThat(retrievedSpace).isNotNull();
        assertThat(retrievedSpace.name()).isEqualTo("TestSpace");
    }

    @Test
    @DisplayName("SpaceDAOInMemory test: Get Space by name - Success")
    void getSpaceByName_shouldGetSpace_whenExists() {
        spaceDAO.add(mockSpace);
        Space space = spaceDAO.getSpaceByName("TestSpace");

        assertThat(space).isNotNull();
        assertThat(space.name()).isEqualTo("TestSpace");
    }

    @Test
    @DisplayName("SpaceDAOInMemory test: Get Space by name - Should return null if space does not exist")
    void getSpaceByName_shouldReturnNullIfSpaceDoesNotExist() {
        Space space = spaceDAO.getSpaceByName("NonExistentSpace");

        assertThat(space).isNull();
    }

    @Test
    @DisplayName("SpaceDAOInMemory test: Get All Spaces - Should return all spaces")
    void getSpaces_shouldReturnAllSpaces() {
        Space anotherMockSpace = Mockito.mock(Space.class);
        Mockito.when(anotherMockSpace.name()).thenReturn("AnotherTestSpace");

        spaceDAO.add(mockSpace);
        spaceDAO.add(anotherMockSpace);

        List<Space> spaces = spaceDAO.getSpaces();

        assertThat(spaces).isNotNull();
        assertThat(spaces.size()).isEqualTo(2);
        assertTrue(spaces.contains(mockSpace));
        assertTrue(spaces.contains(anotherMockSpace));
    }

    @Test
    @DisplayName("SpaceDAOInMemory test: Delete Space - Should delete existing space")
    void delete_shouldDeleteSpace() {
        spaceDAO.add(mockSpace);
        spaceDAO.delete("TestSpace");

        Space space = spaceDAO.getSpaceByName("TestSpace");

        assertThat(space).isNull();
    }
}