package dev.makeev.coworking_service_app.model;

import java.time.LocalDate;

/**
 * Represents a range of dates and times for a booking.
 *
 * @param beginningBookingDate  the start date of the booking
 * @param beginningBookingHour  the start hour of the booking
 * @param endingBookingDate    the endingBookingHour date of the booking
 * @param endingBookingHour    the endingBookingHour hour of the booking
 */
public record BookingRange(LocalDate beginningBookingDate,
                           int beginningBookingHour,
                           LocalDate endingBookingDate,
                           int endingBookingHour) {
}
