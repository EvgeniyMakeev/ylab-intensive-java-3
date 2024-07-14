package dev.makeev.coworking_service_app.controller;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dto.*;
import dev.makeev.coworking_service_app.exceptions.BookingNotFoundException;
import dev.makeev.coworking_service_app.service.BookingService;
import dev.makeev.coworking_service_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * REST controller for managing bookings.
 */
@RestController
@RequestMapping(value = "/api/v1/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    /**
     * Constructs a BookingController with the specified BookingService and UserService.
     *
     * @param bookingService the booking service
     * @param userService the user service
     */
    @Autowired
    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    /**
     * Retrieves all bookings.
     *
     * @return a list of BookingDTO
     */
    @LoggingTime
    @PutMapping
    public ResponseEntity<List<BookingDTO>> getBookings(@Validated @RequestBody UserRequestDTO userRequestDTO) {
        userService.checkCredentials(userRequestDTO.login(), userRequestDTO.password());
        List<BookingDTO> bookingsDTOs = userService.isAdmin(userRequestDTO.login())
                ? bookingService.getAllBookingsSortedByUser()
                : bookingService.getAllBookingsForUser(userRequestDTO.login());

        return ResponseEntity.ok(bookingsDTOs);
    }

    /**
     * Adds a new booking.
     *
     * @param bookingAddDTO the booking data
     * @return an ApiResponse indicating success or failure
     */
    @LoggingTime
    @PostMapping
    public ResponseEntity<ApiResponse> addBooking(@Validated @RequestBody BookingAddDTO bookingAddDTO) {
        userService.checkCredentials(bookingAddDTO.login(), bookingAddDTO.password());
        if (isValidTime(bookingAddDTO)) {
            bookingService.addBooking(bookingAddDTO.login(), bookingAddDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Booking added successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Error in the parameters of the submitted request"));
        }
    }

    /**
     * Checks if the booking time is valid.
     *
     * @param bookingAddDTO the booking data to validate
     * @return true if the booking time is valid, false otherwise
     */
    private boolean isValidTime(BookingAddDTO bookingAddDTO) {
        int minHourOfBeginning = 0;
        int maxHourOfEnding = 24;

        if (bookingAddDTO.beginningBookingDate().equalsIgnoreCase(bookingAddDTO.endingBookingDate())
                && bookingAddDTO.beginningBookingHour() > bookingAddDTO.endingBookingHour()) {
            return false;
        }

        return bookingAddDTO.beginningBookingHour() >= minHourOfBeginning
                && bookingAddDTO.beginningBookingHour() < maxHourOfEnding
                && bookingAddDTO.endingBookingHour() > minHourOfBeginning
                && bookingAddDTO.endingBookingHour() <= maxHourOfEnding;
    }

    /**
     * Deletes a booking.
     *
     * @param bookingRequestDTO the booking data
     * @return an ApiResponse indicating success or failure
     */
    @LoggingTime
    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteBookings(@Validated @RequestBody BookingRequestDTO bookingRequestDTO) {
        userService.checkCredentials(bookingRequestDTO.login(), bookingRequestDTO.password());
        if (userService.isAdmin(bookingRequestDTO.login())) {
            bookingService.deleteBookingById(bookingRequestDTO.login(), bookingRequestDTO.id());
        } else {
            if (bookingService.getAllBookingsForUser(bookingRequestDTO.login()).stream()
                    .anyMatch(booking -> Objects.equals(booking.id(), bookingRequestDTO.id()))) {
                bookingService.deleteBookingById(bookingRequestDTO.login(), bookingRequestDTO.id());
            } else {
                throw new BookingNotFoundException();
            }
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse("Booking deleted successfully"));
    }
}