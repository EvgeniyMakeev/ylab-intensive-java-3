package dev.makeev.coworking_service_app.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.aop.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dao.implementation.BookingDAOInBd;
import dev.makeev.coworking_service_app.dao.implementation.SpaceDAOInBd;
import dev.makeev.coworking_service_app.dao.implementation.UserDAOInBd;
import dev.makeev.coworking_service_app.dto.BookingAddDTO;
import dev.makeev.coworking_service_app.dto.BookingDTO;
import dev.makeev.coworking_service_app.dto.BookingRequestDTO;
import dev.makeev.coworking_service_app.dto.UserRequestDTO;
import dev.makeev.coworking_service_app.exceptions.BookingNotFoundException;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailableException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;
import dev.makeev.coworking_service_app.exceptions.VerificationException;
import dev.makeev.coworking_service_app.mappers.ApiResponse;
import dev.makeev.coworking_service_app.mappers.BookingMapper;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.service.BookingService;
import dev.makeev.coworking_service_app.service.UserService;
import dev.makeev.coworking_service_app.util.ConnectionManager;
import dev.makeev.coworking_service_app.util.implementation.ConnectionManagerImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

@WebServlet("/api/v1/bookings")
public class BookingServlet extends HttpServlet {
    public static final String CONTENT_TYPE = "application/json";


    private BookingService bookingService;
    private UserService userService;
    private ObjectMapper objectMapper;
    private final BookingMapper bookingMapper = BookingMapper.INSTANCE;

    @Override
    public void init() {
        ConnectionManager connectionManager = new ConnectionManagerImpl();
        bookingService = new BookingService(
                new BookingDAOInBd(connectionManager), 
                new SpaceDAOInBd(connectionManager));
        userService = new UserService(new UserDAOInBd(connectionManager));
        objectMapper = new ObjectMapper();
    }

    @LoggingTime
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        try (OutputStream outputStream = response.getOutputStream()){
            InputStream inputStream = request.getInputStream();
            if (inputStream == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            UserRequestDTO userRequestDTO = objectMapper.readValue(inputStream, UserRequestDTO.class);
            checkingRequest(response, userRequestDTO);

            List<Booking> bookings = userService.isAdmin(userRequestDTO.login())
                    ? bookingService.getAllBookingsSortedByUser()
                    : bookingService.getAllBookingsForUser(userRequestDTO.login());

            List<BookingDTO> bookingsDTOs = bookings
                    .stream()
                    .map(bookingMapper::toBookingDTO)
                    .toList();

            objectMapper.writeValue(outputStream, bookingsDTOs);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (VerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch (DaoException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        }
    }

    private void checkingRequest(HttpServletResponse response, UserRequestDTO userRequestDTO) throws IOException {
        if (userRequestDTO == null || userRequestDTO.login() == null || userRequestDTO.password() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(), new ApiResponse("All parameters are required"));
        } else {
            userService.checkCredentials(userRequestDTO.login(), userRequestDTO.password());
        }
    }

    @LoggingTime
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        try {
           BookingAddDTO bookingAddDTO = objectMapper.readValue(request.getInputStream(), BookingAddDTO.class);

            if (isValid(bookingAddDTO)) {
                userService.checkCredentials(bookingAddDTO.loginOfUser(), bookingAddDTO.password());
                Booking booking = bookingMapper.toBooking(bookingAddDTO);
                bookingService.addBooking(bookingAddDTO.loginOfUser(), booking);

                response.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(response.getWriter(), new ApiResponse("Booking added successfully"));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(response.getWriter(), new ApiResponse("All parameters are required"));
            }

        } catch (VerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch (SpaceNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch (SpaceIsNotAvailableException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch (DaoException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        }
    }

    private boolean isValid(BookingAddDTO bookingAddDTO) {
        return bookingAddDTO == null
                || bookingAddDTO.loginOfUser() == null
                || bookingAddDTO.nameOfBookingSpace() != null
                || bookingAddDTO.beginningBookingDate() != null
                || bookingAddDTO.endingBookingDate() != null
                || isValidTime(bookingAddDTO);
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
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        try {
            BookingRequestDTO bookingRequestDTO = objectMapper.readValue(request.getInputStream(), BookingRequestDTO.class);
            userService.checkCredentials(bookingRequestDTO.login(), bookingRequestDTO.password());
            if (userService.isAdmin(bookingRequestDTO.login())) {
                bookingService.deleteBookingById(bookingRequestDTO.login(), bookingRequestDTO.id());
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getWriter(), new ApiResponse("Booking deleted successfully"));
            } else {
                if (bookingService.getAllBookingsForUser(bookingRequestDTO.login()).stream()
                        .anyMatch(booking -> Objects.equals(booking.id(), bookingRequestDTO.id()))) {
                    bookingService.deleteBookingById(bookingRequestDTO.login(), bookingRequestDTO.id());
                } else {
                    throw new BookingNotFoundException();
                }
            }
        } catch (VerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch (BookingNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch (DaoException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        }
    }
}