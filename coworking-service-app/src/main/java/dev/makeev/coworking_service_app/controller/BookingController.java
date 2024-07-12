package dev.makeev.coworking_service_app.controller;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dto.ApiResponse;
import dev.makeev.coworking_service_app.dto.BookingAddDTO;
import dev.makeev.coworking_service_app.dto.BookingDTO;
import dev.makeev.coworking_service_app.dto.BookingRequestDTO;
import dev.makeev.coworking_service_app.dto.UserRequestDTO;
import dev.makeev.coworking_service_app.exceptions.BookingNotFoundException;
import dev.makeev.coworking_service_app.mappers.BookingMapper;
import dev.makeev.coworking_service_app.model.Booking;
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

@RestController
@RequestMapping(value = "/v1/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingController(BookingService bookingService, UserService userService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.bookingMapper = bookingMapper;
    }

    @LoggingTime
    @PutMapping
    public ResponseEntity<List<BookingDTO>> getBookings(@Validated @RequestBody UserRequestDTO userRequestDTO) {
        userService.checkCredentials(userRequestDTO.login(), userRequestDTO.password());
        List<Booking> bookings = userService.isAdmin(userRequestDTO.login())
                ? bookingService.getAllBookingsSortedByUser()
                : bookingService.getAllBookingsForUser(userRequestDTO.login());

        List<BookingDTO> bookingDTOs = bookings.stream()
                .map(bookingMapper::toBookingDTO)
                .toList();
        return ResponseEntity.ok(bookingDTOs);
    }

    @LoggingTime
    @PostMapping
    public ResponseEntity<ApiResponse> addBooking(@Validated @RequestBody BookingAddDTO bookingAddDTO) {
        userService.checkCredentials(bookingAddDTO.login(), bookingAddDTO.password());
        if (isValidTime(bookingAddDTO)) {
            Booking booking = bookingMapper.toBooking(bookingAddDTO);
            bookingService.addBooking(bookingAddDTO.login(), booking);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Booking added successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("All parameters are required"));
        }
    }

    private boolean isValidTime(BookingAddDTO bookingAddDTO) {
        int minHourOfBeginning = 0;
        int maxHourOfEnding = 24;

        return bookingAddDTO.beginningBookingHour() >= minHourOfBeginning
                && bookingAddDTO.beginningBookingHour() < maxHourOfEnding
                && bookingAddDTO.endingBookingHour() > minHourOfBeginning
                && bookingAddDTO.endingBookingHour() <= maxHourOfEnding;
    }

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