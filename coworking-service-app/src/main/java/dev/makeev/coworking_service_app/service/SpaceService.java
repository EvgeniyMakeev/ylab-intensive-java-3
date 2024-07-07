package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.aop.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.dto.SpaceAddDTO;
import dev.makeev.coworking_service_app.exceptions.SpaceAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.WorkingHours;

import java.time.LocalDate;
import java.util.*;

/**
 * Service class for managing coworking spaces.
 */
public final class SpaceService {

    private final SpaceDAO spaceDAO;
    /**
     * Constructs a new SpaceService.
     *
     * @param spaceDAO the SpaceDAO to use for space operations
     */
    public SpaceService(SpaceDAO spaceDAO) {
        this.spaceDAO = spaceDAO;
    }

    /**
     * Adds a new space or updates an existing space.
     *
     * @param spaceAddDTO the spaceDTO
     */
    @LoggingTime
    public void addSpace(SpaceAddDTO spaceAddDTO) throws SpaceAlreadyExistsException, SpaceNotFoundException {
        WorkingHours workingHours = new WorkingHours(spaceAddDTO.hourOfBeginningWorkingDay(), spaceAddDTO.hourOfEndingWorkingDay());

        if (spaceDAO.getSpaceByName(spaceAddDTO.name()).isPresent()) {
            throw new SpaceAlreadyExistsException();
        }

        LocalDate nowDate = LocalDate.now();
        Map<LocalDate, Map<Integer, Long>> bookingSlots = new HashMap<>();

        Map<Integer, Long> slots = new HashMap<>();
        long freeSlot = 0L;
        for (int i = spaceAddDTO.hourOfBeginningWorkingDay(); i < spaceAddDTO.hourOfEndingWorkingDay(); i++) {
            slots.put(i, freeSlot);
        }

        for (int i = 0; i < spaceAddDTO.numberOfDaysAvailableForBooking(); i++) {
            bookingSlots.put(nowDate.plusDays(i), slots);
        }

        spaceDAO.add(new Space(spaceAddDTO.name(), workingHours, bookingSlots));
    }

    /**
     * Retrieves a list of all spaces.
     *
     * @return a list of all spaces
     */
    @LoggingTime
    public List<String> getSpaces() {
        return spaceDAO.getNamesOfSpaces();
    }

    @LoggingTime
    public Optional<Space> getSpacesByName(String name) {
        return spaceDAO.getSpaceByName(name);
    }

    /**
     * Deletes a space by its name.
     *
     * @param nameOfSpace the name of the space to delete
     */
    public void deleteSpace(String nameOfSpace) throws SpaceNotFoundException {
        spaceDAO.getSpaceByName(nameOfSpace).orElseThrow(SpaceNotFoundException::new);
        spaceDAO.delete(nameOfSpace);
    }

    public List<String> getAvailableSlotsForBooking(String name) throws SpaceNotFoundException {
        Space space = spaceDAO.getSpaceByName(name).orElseThrow(SpaceNotFoundException::new);

        List<String> availableSlots = new ArrayList<>();
        availableSlots.add(space.name() + " - available slots for booking:");

        long freeSlot = 0L;

        space.bookingSlots().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(dateEntry -> {
                    availableSlots.add(dateEntry.getKey().toString());
                    StringBuilder stringBuilder = new StringBuilder();
                    dateEntry.getValue().keySet().stream()
                            .filter(hour -> dateEntry.getValue().get(hour) == freeSlot)
                            .sorted(Comparator.naturalOrder())
                            .forEachOrdered(hour ->
                                    stringBuilder.append(String.format("%02d:00 - %02d:00", hour, hour + 1))
                                    .append(" | "));
                    availableSlots.add(stringBuilder.toString());
                });

        return availableSlots;
    }
}
