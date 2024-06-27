package dev.makeev.coworking_service_app.model;

/**
 * Represents a booking made by a user for a specific space and time range.
 *
 * @param id            the unique identifier of the booking
 * @param loginOfUser   the login of user of the booking
 * @param nameOfBookingSpace  the name of space being booked
 * @param bookingRange  the range of the booking
 */
public record Booking(Long id,
                      String loginOfUser,
                      String nameOfBookingSpace,
                      BookingRange bookingRange) {

    /**
     * Constructs a new Booking with the specified space and booking range.
     * The booking ID is automatically generated.
     *
     * @param loginOfUser the space being booked
     * @param nameOfBookingSpace  the name of space being booked
     * @param bookingRange  the range of the booking
     */
    public Booking(String loginOfUser, String nameOfBookingSpace, BookingRange bookingRange) {
        this(-1L,
                loginOfUser,
                nameOfBookingSpace,
                bookingRange);
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %d | Space: %s | From: %02d:00 %s | To: %02d:00 %s | By: %s\n",
                id,
                nameOfBookingSpace,
                bookingRange.beginningBookingHour(),
                bookingRange.beginningBookingDate(),
                bookingRange.endingBookingHour(),
                bookingRange.endingBookingDate(),
                loginOfUser);
    }
}
