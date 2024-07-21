package dev.makeev.coworking_service_app.controller;

import dev.makeev.coworking_service_app.dto.ApiResponse;
import dev.makeev.coworking_service_app.dto.BookingAddDTO;
import dev.makeev.coworking_service_app.dto.BookingDTO;
import dev.makeev.coworking_service_app.exceptions.BadRequestException;
import dev.makeev.coworking_service_app.service.BookingService;
import dev.makeev.coworking_service_app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SecurityRequirement(name = "apiKeyScheme")
@Tag(name = "Bookings", description = "Bookings API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @Operation(summary = "Get all bookings")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    List<BookingDTO> getBookings(HttpServletRequest request) {
        String login = (String) request.getAttribute("login");
        return userService.isAdmin(login)
                ? bookingService.getAllBookingsSortedByUser()
                : bookingService.getAllBookingsForUser(login);
    }

    @Operation(summary = "Add new bookings")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    ApiResponse addBooking(HttpServletRequest request,
                           @Validated @RequestBody BookingAddDTO bookingAddDTO) {
        String login = (String) request.getAttribute("login");
        if (isValidTime(bookingAddDTO)) {
            bookingService.addBooking(login, bookingAddDTO);
            return new ApiResponse("Booking added successfully");
        } else {
            throw new BadRequestException();
        }
    }

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

    @Operation(summary = "Delete bookings by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    ApiResponse deleteBookings(HttpServletRequest request,
                                                      @PathVariable long id) {
        String login = (String) request.getAttribute("login");
        if (userService.isAdmin(login)) {
            bookingService.deleteBookingByIdByAdmin(login, id);
        } else {
            bookingService.deleteBookingById(login, id);
        }
        return new ApiResponse("Booking with ID:" + id + " deleted successfully");
    }
}
