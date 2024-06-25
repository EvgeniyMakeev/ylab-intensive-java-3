package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.WorkingHours;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing coworking spaces.
 */
public class SpaceService {

    private final SpaceDAO spaceDAO;
    private final BookingService bookingService;

    /**
     * Constructs a new SpaceService.
     *
     * @param spaceDAO the SpaceDAO to use for space operations
     * @param bookingService the BookingService to use for booking operations
     */
    public SpaceService(SpaceDAO spaceDAO, BookingService bookingService) {
        this.spaceDAO = spaceDAO;
        this.bookingService = bookingService;
    }

    /**
     * Adds a new space or updates an existing space.
     *
     * @param nameOfSpace the name of the space
     * @param hourOfStartWorkingDay the start hour of the working day
     * @param hourOfEndWorkingDay the end hour of the working day
     * @param numberOfDaysAvailableForBooking the number of days available for booking
     */
    public void addAndUpdateSpace(
            String nameOfSpace, int hourOfStartWorkingDay, int hourOfEndWorkingDay, int numberOfDaysAvailableForBooking) {
        WorkingHours workingHours = new WorkingHours(hourOfStartWorkingDay, hourOfEndWorkingDay);
        spaceDAO.add(new Space(nameOfSpace, workingHours, initFreeSlotsForBooking(
                workingHours, numberOfDaysAvailableForBooking)));
    }

    /**
     * Retrieves a list of all spaces.
     *
     * @return a list of all spaces
     */
    public List<Space> getSpaces() {
        return spaceDAO.getSpaces();
    }

    /**
     * Deletes a space by its name.
     *
     * @param nameOfSpace the name of the space to delete
     */
    public void deleteSpace(String nameOfSpace) {
        bookingService.deleteBookingsBySpace(nameOfSpace);
        spaceDAO.delete(nameOfSpace);
    }

    /**
     * Initializes free slots for booking for a given number of days and working hours.
     *
     * @param workingHours the working hours of the space
     * @param numberOfDaysAvailableForBooking the number of days available for booking
     * @return a map of booking slots initialized to free
     */
    static Map<LocalDate, Map<Integer, Boolean>> initFreeSlotsForBooking(
            WorkingHours workingHours, int numberOfDaysAvailableForBooking) {
        Map<LocalDate, Map<Integer, Boolean>> bookingSlots = new HashMap<>();
        LocalDate nowDate = LocalDate.now();
        for (int i = 0; i < numberOfDaysAvailableForBooking; i++) {
            Map<Integer, Boolean> availableHours = new HashMap<>();
            for (int j = 0; j < workingHours.hourOfEndWorkingDay() - workingHours.hourOfStartWorkingDay(); j++) {
                availableHours.put(workingHours.hourOfStartWorkingDay() + j, true);
            }
            bookingSlots.put(nowDate.plusDays(i), availableHours);
        }
        return bookingSlots;
    }
}
