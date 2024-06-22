package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.WorkingHours;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpaceService {

    private final SpaceDAO spaceDAO;
    private final BookingService bookingService;

    public SpaceService(SpaceDAO spaceDAO, BookingService bookingService) {
        this.spaceDAO = spaceDAO;
        this.bookingService = bookingService;
    }

    public void init() {
        addAndUpdateSpace("Workplace No. 1", 8, 20, 20);
        addAndUpdateSpace("Conference hall", 10, 18, 15);
    }


    public void addAndUpdateSpace(
            String nameOfSpace, int hourOfStartWorkingDay, int hourOfEndWorkingDay, int numberOfDaysAvailableForBooking) {
        WorkingHours workingHours = new WorkingHours(hourOfStartWorkingDay, hourOfEndWorkingDay);
        spaceDAO.add(new Space(nameOfSpace, workingHours, initFreeSlotsForBooking(
                workingHours, numberOfDaysAvailableForBooking)));
    }

    public List<Space> getSpaces() {
        return spaceDAO.getSpaces();
    }

    public void deleteSpace(String nameOfSpace) {
        bookingService.deleteBookingsBySpace(nameOfSpace);
        spaceDAO.delete(nameOfSpace);
    }

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

