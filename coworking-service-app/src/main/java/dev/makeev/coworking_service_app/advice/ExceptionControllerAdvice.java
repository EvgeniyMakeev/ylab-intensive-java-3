package dev.makeev.coworking_service_app.advice;

import dev.makeev.coworking_service_app.dto.ErrorDetails;
import dev.makeev.coworking_service_app.exceptions.AuthorizationHeaderException;
import dev.makeev.coworking_service_app.exceptions.BookingNotFoundException;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.exceptions.LoginAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.NoAdminException;
import dev.makeev.coworking_service_app.exceptions.SpaceAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailableException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;
import dev.makeev.coworking_service_app.exceptions.TokenVerificationException;
import dev.makeev.coworking_service_app.exceptions.VerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST controllers.
 * Handles specific exceptions and maps them to appropriate HTTP status codes and error responses.
 */
@RestControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * Handles VerificationException and maps it to HTTP 401 UNAUTHORIZED status.
     */
    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<ErrorDetails> handleVerificationException(VerificationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDetails(e.getMessage()));
    }

    /**
     * Handles AuthorizationHeaderException and maps it to HTTP 401 UNAUTHORIZED status.
     */
    @ExceptionHandler(AuthorizationHeaderException.class)
    public ResponseEntity<ErrorDetails> handleAuthorizationHeaderException(AuthorizationHeaderException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDetails(e.getMessage()));
    }

    /**
     * Handles TokenVerificationException and maps it to HTTP 401 UNAUTHORIZED status.
     */
    @ExceptionHandler(TokenVerificationException.class)
    public ResponseEntity<ErrorDetails> handleTokenVerificationException(TokenVerificationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDetails(e.getMessage()));
    }

    /**
     * Handles NoAdminException and maps it to HTTP 401 UNAUTHORIZED status.
     */
    @ExceptionHandler(NoAdminException.class)
    public ResponseEntity<ErrorDetails> handleNoAdminException(NoAdminException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDetails(e.getMessage()));
    }

    /**
     * Handles LoginAlreadyExistsException and maps it to HTTP 409 CONFLICT status.
     */
    @ExceptionHandler(LoginAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleLoginAlreadyExistsException(LoginAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDetails(e.getMessage()));
    }

    /**
     * Handles SpaceAlreadyExistsException and maps it to HTTP 409 CONFLICT status.
     */
    @ExceptionHandler(SpaceAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleSpaceAlreadyExistsException(SpaceAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDetails(e.getMessage()));
    }

    /**
     * Handles BookingNotFoundException and maps it to HTTP 404 NOT_FOUND status.
     */
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleBookingNotFoundException(BookingNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDetails(e.getMessage()));
    }

    /**
     * Handles SpaceNotFoundException and maps it to HTTP 404 NOT_FOUND status.
     */
    @ExceptionHandler(SpaceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleSpaceNotFoundException(SpaceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDetails(e.getMessage()));
    }

    /**
     * Handles SpaceIsNotAvailableException and maps it to HTTP 409 CONFLICT status.
     */
    @ExceptionHandler(SpaceIsNotAvailableException.class)
    public ResponseEntity<ErrorDetails> handleSpaceIsNotAvailableException(SpaceIsNotAvailableException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDetails(e.getMessage()));
    }

    /**
     * Handles DaoException and maps it to HTTP 500 INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(DaoException.class)
    public ResponseEntity<ErrorDetails> handleInternalServerError(DaoException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDetails(e.getMessage()));
    }
}
