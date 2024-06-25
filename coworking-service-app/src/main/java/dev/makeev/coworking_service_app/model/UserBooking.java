package dev.makeev.coworking_service_app.model;

/**
 * Represents a booking made by a user.
 *
 * @param userLogin  the login of the user who made the booking
 * @param booking    the booking details
 */
public record UserBooking(String userLogin,
                          Booking booking) {

    /**
     * Formats the booking details as a string.
     *
     * @return a formatted string of the booking details
     */
    public String format() {
        return String.format(
                "ID: %d | Space: %s | From: %02d:00 %s | To: %02d:00 %s | By: %s\n",
                booking.id(),
                booking.bookingSpace().name(),
                booking.bookingRange().hourBookingFrom(),
                booking.bookingRange().dateBookingFrom(),
                booking.bookingRange().hourBookingTo(),
                booking.bookingRange().dateBookingTo(),
                userLogin
        );
    }
}
