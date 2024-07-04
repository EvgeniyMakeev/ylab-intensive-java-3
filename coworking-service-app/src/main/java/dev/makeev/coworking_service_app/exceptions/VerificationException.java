package dev.makeev.coworking_service_app.exceptions;

/**
 * The {@code VerificationException} class is an exception that is thrown when access verification fails,
 * typically due to incorrect login credentials. It extends the {@code Exception} class and provides a
 * custom error message.
 */
public class VerificationException extends RuntimeException {

    /**
     * Overrides the {@code getMessage} method to provide a custom error message for the verification exception.
     *
     * @return A string representing the error message for the verification exception.
     */
    @Override
    public String getMessage() {
        return """
                Access is denied!
                Wrong login or password.""";
    }
}

