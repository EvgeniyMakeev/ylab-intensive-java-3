package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.advice.annotations.LoggingToDb;
import dev.makeev.coworking_service_app.dto.SpaceAddDTO;
import dev.makeev.coworking_service_app.exceptions.SpaceAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;

import java.util.List;

public interface SpaceService {
    @LoggingTime
    void addSpace(SpaceAddDTO spaceAddDTO) throws SpaceAlreadyExistsException, SpaceNotFoundException;

    @LoggingTime
    @LoggingToDb
    List<String> getNamesOfSpaces();

    @LoggingTime
    void deleteSpace(String nameOfSpace) throws SpaceNotFoundException;
}
