package dev.makeev.coworking_service_app.exceptions;

/**
 * Exception thrown when a space with this name already exists.
 */
public class SpaceAlreadyExistsException extends Exception {

    /**
     * Returns the exception message indicating space this name already exists.
     *
     * @return the exception message
     */
    @Override
    public String getMessage() {
        return "A space with this name already exists.";
    }
}