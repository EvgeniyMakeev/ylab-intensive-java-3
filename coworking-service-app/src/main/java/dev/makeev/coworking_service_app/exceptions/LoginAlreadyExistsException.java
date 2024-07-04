package dev.makeev.coworking_service_app.exceptions;

/**
 * The {@code LoginAlreadyExistsException} class is an exception that is thrown when attempting to add a user with a login
 * that already exists in the system. It extends the {@code Exception} class and provides a custom error message.
 */
public class LoginAlreadyExistsException extends RuntimeException {

    /**
     * Overrides the {@code getMessage} method to provide a custom error message for the login already exists exception.
     *
     * @return A string representing the error message for the login already exists exception.
     */
    @Override
    public String getMessage() {
        return "A user with this login already exists!";
    }
}
