package dev.makeev.coworking_service_app.advice;

import dev.makeev.coworking_service_app.dto.ErrorDetails;
import dev.makeev.coworking_service_app.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<ErrorDetails> handleLoginVerificationException(VerificationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDetails(e.getMessage()));
    }

    @ExceptionHandler(NoAdminException.class)
    public ResponseEntity<ErrorDetails> handleNoAdminException(NoAdminException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDetails(e.getMessage()));
    }

    @ExceptionHandler(LoginAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleLoginAlreadyExistsException(LoginAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDetails(e.getMessage()));
    }

    @ExceptionHandler(SpaceAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleSpaceAlreadyExistsException(SpaceAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDetails(e.getMessage()));
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleBookingNotFoundException(BookingNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDetails(e.getMessage()));
    }

    @ExceptionHandler(SpaceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleSpaceNotFoundException(SpaceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDetails(e.getMessage()));
    }

    @ExceptionHandler(SpaceIsNotAvailableException.class)
    public ResponseEntity<ErrorDetails> handleSpaceIsNotAvailableException(SpaceIsNotAvailableException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDetails(e.getMessage()));
    }

    @ExceptionHandler(DaoException.class)
    public ResponseEntity<ErrorDetails> handleInternalServerError(DaoException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDetails(e.getMessage()));
    }
}