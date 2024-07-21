package dev.makeev.coworking_service_app.exceptions;

/**
 * The {@code TokenVerificationException} class is an exception that is thrown when access verification fails,
 * typically due to incorrect token. It extends the {@code Exception} class and provides a
 * custom error message.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Overrides the {@code getMessage} method to provide a custom error message for the verification exception.
     *
     * @return A string representing the error message for the verification exception.
     */
    @Override
    public String getMessage() {
        return "Error in the parameters of the submitted request";
    }
}

