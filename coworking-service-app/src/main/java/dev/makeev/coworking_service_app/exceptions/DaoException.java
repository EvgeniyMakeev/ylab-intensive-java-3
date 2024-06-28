package dev.makeev.coworking_service_app.exceptions;

/**
 * Custom exception class for handling Data Access Object (DAO) related exceptions.
 */
public class DaoException extends RuntimeException {

    /**
     * Constructs a new DaoException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public DaoException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new DaoException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
