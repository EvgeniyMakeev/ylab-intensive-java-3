package dev.makeev.coworking_service_app.model;

import java.time.LocalDate;
import java.util.Map;

/**
 * Represents a coworking space with specific working hours and booking slots.
 *
 * @param name          the name of the space
 * @param workingHours  the working hours of the space
 * @param bookingSlots  the booking slots available for the space
 */
public record Space(String name,
                    WorkingHours workingHours,
                    Map<LocalDate, Map<Integer, Long>> bookingSlots) {
}
