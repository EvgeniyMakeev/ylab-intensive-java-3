package dev.makeev.coworking_service_app.model;

/**
 * Represents a booking made by a user for a specific space and time range.
 *
 * @param id            the unique identifier of the booking
 * @param login   the login of user of the booking
 * @param nameOfBookingSpace  the name of space being booked
 * @param bookingRange  the range of the booking
 */
public record Booking(Long id,
                      String login,
                      String nameOfBookingSpace,
                      BookingRange bookingRange) {

    @Override
    public String toString() {
        return nameOfBookingSpace +
                " from " + bookingRange.beginningBookingHour() + ":00 " + bookingRange.beginningBookingDate() +
                " to " + bookingRange.endingBookingHour() + ":00 " + bookingRange.endingBookingDate();
    }

    /**
     * Constructs a new Booking with the specified space and booking range.
     * The booking ID is automatically generated.
     *
     * @param login the space being booked
     * @param nameOfBookingSpace  the name of space being booked
     * @param bookingRange  the range of the booking
     */
    public Booking(String login, String nameOfBookingSpace, BookingRange bookingRange) {
        this(-1L,
                login,
                nameOfBookingSpace,
                bookingRange);
    }
}
