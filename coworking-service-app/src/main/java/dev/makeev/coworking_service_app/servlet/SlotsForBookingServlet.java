package dev.makeev.coworking_service_app.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.aop.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dao.implementation.SpaceDAOInBd;
import dev.makeev.coworking_service_app.dto.SlotsAvailableForBookingDTO;
import dev.makeev.coworking_service_app.dto.SpaceSlotsDTO;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;
import dev.makeev.coworking_service_app.mappers.ApiResponse;
import dev.makeev.coworking_service_app.service.SpaceService;
import dev.makeev.coworking_service_app.util.implementation.ConnectionManagerImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@WebServlet("/api/v1/slots")
public class SlotsForBookingServlet extends HttpServlet {
    public static final String CONTENT_TYPE = "application/json";

    private SpaceService spaceService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        spaceService = new SpaceService(new SpaceDAOInBd(new ConnectionManagerImpl()));
        objectMapper = new ObjectMapper();
    }

    @LoggingTime
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        try (OutputStream outputStream = response.getOutputStream()){
            SpaceSlotsDTO spaceSlotsDTO = objectMapper.readValue(request.getInputStream(), SpaceSlotsDTO.class);
            if (spaceSlotsDTO != null) {
                List<SlotsAvailableForBookingDTO> slotsForBooking =
                        spaceService.getAvailableSlotsForBooking(spaceSlotsDTO.name());
                objectMapper.writeValue(outputStream, slotsForBooking);
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (SpaceNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch (DaoException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        }
    }
}