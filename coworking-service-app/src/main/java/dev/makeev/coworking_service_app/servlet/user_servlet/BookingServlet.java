package dev.makeev.coworking_service_app.servlet.user_servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.aop.annotations.Loggable;
import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.dao.implementation.BookingDAOInBd;
import dev.makeev.coworking_service_app.dao.implementation.SpaceDAOInBd;
import dev.makeev.coworking_service_app.dto.BookingDTO;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.mappers.ApiResponse;
import dev.makeev.coworking_service_app.mappers.BookingMapper;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.service.BookingService;
import dev.makeev.coworking_service_app.service.SpaceService;
import dev.makeev.coworking_service_app.util.ConnectionManager;
import dev.makeev.coworking_service_app.util.implementation.ConnectionManagerImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Loggable
@WebServlet("/api/v1/bookings")
public class BookingServlet extends HttpServlet {
    public static final String CONTENT_TYPE = "application/json";



    private BookingService bookingService;
    private SpaceService spaceService;
    private ObjectMapper objectMapper;
    private final BookingMapper bookingMapper = BookingMapper.INSTANCE;

    @Override
    public void init() {
        ConnectionManager connectionManager = new ConnectionManagerImpl();
        SpaceDAO spaceDAO = new SpaceDAOInBd(connectionManager);
        bookingService = new BookingService(new BookingDAOInBd(connectionManager), spaceDAO);
        spaceService = new SpaceService(spaceDAO);
        objectMapper = new ObjectMapper();
        System.out.println("BookingServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        try {
            List<Booking> bookings = bookingService.getAllBookingsSortedByUser();

            List<BookingDTO> bookingsDTOs = bookings
                    .stream()
                    .map(bookingMapper::toBookingDTO)
                    .toList();
            objectMapper.writeValue(response.getOutputStream(), bookingsDTOs);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (DaoException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        }
    }
}