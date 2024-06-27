package dev.makeev.coworking_service_app.dao;

import dev.makeev.coworking_service_app.model.Space;

import java.util.List;
import java.util.Optional;


/**
 * DAO interface for managing coworking spaces.
 */
public interface SpaceDAO {
    /**
     * Adds a new space.
     *
     * @param newSpace the space to be added
     */
    void add(Space newSpace);

    /**
     * Retrieves all spaces.
     *
     * @return a list of all spaces
     */
    List<String> getNamesOfSpaces();

    /**
     * Retrieves a space by its name.
     *
     * @param nameOfSpace the name of the space
     * @return the space with the specified name
     */
    Optional<Space> getSpaceByName(String nameOfSpace);

    /**
     * Deletes a space by its name.
     *
     * @param nameOfSpace the name of the space to be deleted
     */
    void delete(String nameOfSpace);
}
