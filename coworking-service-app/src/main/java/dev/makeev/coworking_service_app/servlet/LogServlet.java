//package dev.makeev.coworking_service_app.servlet;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import dev.makeev.coworking_service_app.aop.annotations.LoggingTime;
//import dev.makeev.coworking_service_app.dao.implementation.LogDaoInBd;
//import dev.makeev.coworking_service_app.dao.implementation.SpaceDAOInBd;
//import dev.makeev.coworking_service_app.dto.BookingDTO;
//import dev.makeev.coworking_service_app.exceptions.DaoException;
//import dev.makeev.coworking_service_app.mappers.ApiResponse;
//import dev.makeev.coworking_service_app.model.LogOfUserAction;
//import dev.makeev.coworking_service_app.service.LogService;
//import dev.makeev.coworking_service_app.service.SpaceService;
//import dev.makeev.coworking_service_app.util.implementation.ConnectionManagerImpl;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.List;
//
//@WebServlet("/api/v1/logs")
//public class LogServlet extends HttpServlet {
//    public static final String CONTENT_TYPE = "application/json";
//
//    private LogService logService;
//    private ObjectMapper objectMapper;
//
//    @Override
//    public void init() {
//        logService = new LogService(new LogDaoInBd(new ConnectionManagerImpl()));
//        objectMapper = new ObjectMapper();
//    }
//
//    @LoggingTime
//    @Override
//    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType(CONTENT_TYPE);
//        try (OutputStream outputStream = response.getOutputStream()){
//            List<LogOfUserAction> logOfUserActions = logService.getLogs();
//
//            List<BookingDTO> bookingsDTOs = bookings
//                    .stream()
//                    .map(bookingMapper::toBookingDTO)
//                    .toList();
//
//            objectMapper.writeValue(outputStream, spaces);
//            response.setStatus(HttpServletResponse.SC_OK);
//
//        } catch (DaoException e) {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
//        }
//    }
//}