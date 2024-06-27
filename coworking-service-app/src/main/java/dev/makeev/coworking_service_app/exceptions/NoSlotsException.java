package dev.makeev.coworking_service_app.exceptions;

/**
 * Exception thrown when a space is not available for booking.
 */
public class NoSlotsException extends Exception {

    /**
     * Returns the exception message indicating that the space is not available.
     *
     * @return the exception message
     */
    @Override
    public String getMessage() {
        return "No free slots for booking.";
    }
}