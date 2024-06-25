package dev.makeev.coworking_service_app.model;

/**
 * Represents a booking made by a user for a specific space and time range.
 *
 * @param id            the unique identifier of the booking
 * @param bookingSpace  the space being booked
 * @param bookingRange  the range of the booking
 */
public record Booking(Long id,
                      Space bookingSpace,
                      BookingRange bookingRange) {

    private static long nextId = 1;

    /**
     * Constructs a new Booking with the specified space and booking range.
     * The booking ID is automatically generated.
     *
     * @param bookingSpace  the space being booked
     * @param bookingRange  the range of the booking
     */
    public Booking(Space bookingSpace, BookingRange bookingRange) {
        this(nextId++, bookingSpace, bookingRange);
    }
}