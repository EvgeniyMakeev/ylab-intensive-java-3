package dev.makeev.coworking_service_app.model;

/**
 * Represents the working hours of a coworking space.
 *
 * @param hourOfBeginningWorkingDay  the start hour of the working day
 * @param hourOfEndingWorkingDay    the endingBookingHour hour of the working day
 */
public record WorkingHours(int hourOfBeginningWorkingDay, int hourOfEndingWorkingDay) {
}
