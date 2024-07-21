package dev.makeev.coworking_service_app.advice;

import dev.makeev.coworking_service_app.dto.ErrorDetails;
import dev.makeev.coworking_service_app.exceptions.AuthorizationHeaderException;
import dev.makeev.coworking_service_app.exceptions.BadRequestException;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(VerificationException.class)
    ErrorDetails handleVerificationException(VerificationException e) {
        return new ErrorDetails(e.getMessage());
    }

    /**
     * Handles AuthorizationHeaderException and maps it to HTTP 401 UNAUTHORIZED status.
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthorizationHeaderException.class)
    ErrorDetails handleAuthorizationHeaderException(AuthorizationHeaderException e) {
        return new ErrorDetails(e.getMessage());
    }

    /**
     * Handles TokenVerificationException and maps it to HTTP 401 UNAUTHORIZED status.
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(TokenVerificationException.class)
    ErrorDetails handleTokenVerificationException(TokenVerificationException e) {
        return new ErrorDetails(e.getMessage());
    }

    /**
     * Handles BadRequestException and maps it to HTTP 400 UNAUTHORIZED status.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    ErrorDetails handleBadRequestException(BadRequestException e) {
        return new ErrorDetails(e.getMessage());
    }

    /**
     * Handles NoAdminException and maps it to HTTP 401 UNAUTHORIZED status.
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NoAdminException.class)
    ErrorDetails handleNoAdminException(NoAdminException e) {
        return new ErrorDetails(e.getMessage());
    }

    /**
     * Handles LoginAlreadyExistsException and maps it to HTTP 409 CONFLICT status.
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(LoginAlreadyExistsException.class)
    ErrorDetails handleLoginAlreadyExistsException(LoginAlreadyExistsException e) {
        return new ErrorDetails(e.getMessage());
    }

    /**
     * Handles SpaceAlreadyExistsException and maps it to HTTP 409 CONFLICT status.
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(SpaceAlreadyExistsException.class)
    ErrorDetails handleSpaceAlreadyExistsException(SpaceAlreadyExistsException e) {
        return new ErrorDetails(e.getMessage());
    }

    /**
     * Handles BookingNotFoundException and maps it to HTTP 404 NOT_FOUND status.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BookingNotFoundException.class)
    ErrorDetails handleBookingNotFoundException(BookingNotFoundException e) {
        return new ErrorDetails(e.getMessage());
    }

    /**
     * Handles SpaceNotFoundException and maps it to HTTP 404 NOT_FOUND status.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(SpaceNotFoundException.class)
    ErrorDetails handleSpaceNotFoundException(SpaceNotFoundException e) {
        return new ErrorDetails(e.getMessage());
    }

    /**
     * Handles SpaceIsNotAvailableException and maps it to HTTP 409 CONFLICT status.
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(SpaceIsNotAvailableException.class)
    ErrorDetails handleSpaceIsNotAvailableException(SpaceIsNotAvailableException e) {
        return new ErrorDetails(e.getMessage());
    }

    /**
     * Handles DaoException and maps it to HTTP 500 INTERNAL_SERVER_ERROR status.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DaoException.class)
    ErrorDetails handleInternalServerError(DaoException e) {
        return new ErrorDetails(e.getMessage());
    }
}
