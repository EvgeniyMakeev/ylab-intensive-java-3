package dev.makeev.coworking_service_app.model;

public record UserBooking(String userLogin,
                          Booking booking) {
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
