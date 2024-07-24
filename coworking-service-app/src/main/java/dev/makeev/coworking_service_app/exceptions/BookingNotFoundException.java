package dev.makeev.coworking_service_app.exceptions;

/**
 * Exception thrown when a booking is not found.
 */
public class BookingNotFoundException extends RuntimeException {

    /**
     * Returns the exception message indicating a booking is not found.
     *
     * @return the exception message
     */
    @Override
    public String getMessage() {
        return "Booking not found.";
    }
}