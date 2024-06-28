package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.exceptions.SpaceAlreadyExistsException;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.WorkingHours;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * @param nameOfSpace the name of the space
     * @param hourOfBeginningWorkingDay the start hour of the working day
     * @param hourOfEndingWorkingDay the endingBookingHour hour of the working day
     * @param numberOfDaysAvailableForBooking the number of days available for booking
     */
    public void addSpace(
            String nameOfSpace, int hourOfBeginningWorkingDay, int hourOfEndingWorkingDay, int numberOfDaysAvailableForBooking)
            throws SpaceAlreadyExistsException {
        WorkingHours workingHours = new WorkingHours(hourOfBeginningWorkingDay, hourOfEndingWorkingDay);

        if (getSpaceByName(nameOfSpace).isPresent()) {
            throw new SpaceAlreadyExistsException();
        }

        LocalDate nowDate = LocalDate.now();
        Map<LocalDate, Map<Integer, Long>> bookingSlots = new HashMap<>();

        Map<Integer, Long> slots = new HashMap<>();
        long freeSlot = 0L;
        for (int i = hourOfBeginningWorkingDay; i < hourOfEndingWorkingDay; i++) {
            slots.put(i, freeSlot);
        }

        for (int i = 0; i < numberOfDaysAvailableForBooking; i++) {
            bookingSlots.put(nowDate.plusDays(i), slots);
        }

        spaceDAO.add(new Space(nameOfSpace, workingHours, bookingSlots));
    }

    /**
     * Retrieves a list of all spaces.
     *
     * @return a list of all spaces
     */
    public List<String> getSpaces() {
        return spaceDAO.getNamesOfSpaces();
    }


    public Optional<Space> getSpaceByName(String nameOfCurrentSpace) {
        return spaceDAO.getSpaceByName(nameOfCurrentSpace);
    }

    /**
     * Deletes a space by its name.
     *
     * @param nameOfSpace the name of the space to delete
     */
    public void deleteSpace(String nameOfSpace) {
        spaceDAO.delete(nameOfSpace);
    }
}
