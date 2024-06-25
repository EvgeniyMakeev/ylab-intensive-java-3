package dev.makeev.coworking_service_app.model;

import java.time.LocalDate;

/**
 * Represents a range of dates and times for a booking.
 *
 * @param dateBookingFrom  the start date of the booking
 * @param hourBookingFrom  the start hour of the booking
 * @param dateBookingTo    the end date of the booking
 * @param hourBookingTo    the end hour of the booking
 */
public record BookingRange(LocalDate dateBookingFrom,
                           int hourBookingFrom,
                           LocalDate dateBookingTo,
                           int hourBookingTo) {
}
