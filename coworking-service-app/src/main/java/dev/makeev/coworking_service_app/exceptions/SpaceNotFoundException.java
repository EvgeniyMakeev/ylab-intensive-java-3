package dev.makeev.coworking_service_app.exceptions;

/**
 * Exception thrown when a space is not found.
 */
public class SpaceNotFoundException extends RuntimeException {

    /**
     * Returns the exception message indicating a space is not found.
     *
     * @return the exception message
     */
    @Override
    public String getMessage() {
        return "Space not found.";
    }
}