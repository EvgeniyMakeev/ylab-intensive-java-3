package dev.makeev.coworking_service_app.model;

/**
 * Represents the working hours of a coworking space.
 *
 * @param hourOfStartWorkingDay  the start hour of the working day
 * @param hourOfEndWorkingDay    the end hour of the working day
 */
public record WorkingHours(int hourOfStartWorkingDay, int hourOfEndWorkingDay) {
}
