package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.advice.annotations.LoggingToDb;
import dev.makeev.coworking_service_app.dto.SpaceAddDTO;
import dev.makeev.coworking_service_app.dto.SpaceDTO;
import dev.makeev.coworking_service_app.exceptions.SpaceAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;

import java.util.List;

/**
 * SpaceService interface for managing coworking spaces.
 */
public interface SpaceService {

    /**
     * Adds a new space or updates an existing space.
     *
     * @param spaceAddDTO the spaceDTO
     */
    @LoggingTime
    void addSpace(SpaceAddDTO spaceAddDTO) throws SpaceAlreadyExistsException, SpaceNotFoundException;

    /**
     * Retrieves a list of all spaces.
     *
     * @return a list of all spaces
     */
    @LoggingTime
    @LoggingToDb
    List<SpaceDTO> getSpaces();

    /**
     * Deletes a space by its name.
     *
     * @param nameOfSpace the name of the space to delete
     */
    @LoggingTime
    void deleteSpace(String nameOfSpace) throws SpaceNotFoundException;
}
