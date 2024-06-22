package dev.makeev.coworking_service_app.model;

public record Booking(Long id,
                      Space bookingSpace,
                      BookingRange bookingRange) {

    private static long nextId = 1;

    public Booking(Space bookingSpace, BookingRange bookingRange) {
        this(nextId++, bookingSpace, bookingRange);
    }
}
