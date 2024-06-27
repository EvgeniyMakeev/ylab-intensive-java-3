package dev.makeev.coworking_service_app.exceptions;

/**
 * Exception thrown when a space is not available for booking on the specified dates and times.
 */
public class SpaceIsNotAvailableException extends Exception {

    /**
     * Returns the exception message indicating that the space is not available.
     *
     * @return the exception message
     */
    @Override
    public String getMessage() {
        return """
                The space is not available for booking on these dates and times.
                Please choose other dates and times.""";
    }
}