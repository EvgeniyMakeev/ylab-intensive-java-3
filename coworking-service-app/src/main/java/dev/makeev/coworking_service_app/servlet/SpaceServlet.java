package dev.makeev.coworking_service_app.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.aop.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dao.implementation.SpaceDAOInBd;
import dev.makeev.coworking_service_app.dao.implementation.UserDAOInBd;
import dev.makeev.coworking_service_app.dto.SpaceAddDTO;
import dev.makeev.coworking_service_app.dto.SpaceDeleteDTO;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.exceptions.SpaceAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;
import dev.makeev.coworking_service_app.exceptions.VerificationException;
import dev.makeev.coworking_service_app.dto.ApiResponse;
import dev.makeev.coworking_service_app.service.SpaceService;
import dev.makeev.coworking_service_app.service.UserService;
import dev.makeev.coworking_service_app.util.ConnectionManager;
import dev.makeev.coworking_service_app.util.implementation.ConnectionManagerImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@WebServlet("/api/v1/spaces")
public class SpaceServlet extends HttpServlet {
    public static final String CONTENT_TYPE = "application/json";

    private SpaceService spaceService;
    private UserService userService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        ConnectionManager connectionManager = new ConnectionManagerImpl();
        spaceService = new SpaceService(new SpaceDAOInBd(connectionManager));
        userService = new UserService(new UserDAOInBd(connectionManager));
        objectMapper = new ObjectMapper();
    }

    @LoggingTime
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        try (OutputStream outputStream = response.getOutputStream()){
            List<String> spaces = spaceService.getNamesOfSpaces();
            objectMapper.writeValue(outputStream, spaces);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (DaoException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        }
    }

    @LoggingTime
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        try {
            SpaceAddDTO spaceAddDTO = objectMapper.readValue(request.getInputStream(), SpaceAddDTO.class);
            addingSpace(response, spaceAddDTO);
        } catch (VerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch (SpaceAlreadyExistsException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch (DaoException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        }
    }

    private void addingSpace(HttpServletResponse response, SpaceAddDTO spaceAddDTO)
            throws VerificationException, SpaceAlreadyExistsException, IOException {
        if (isValid(spaceAddDTO)) {
            userService.checkCredentials(spaceAddDTO.login(), spaceAddDTO.password());
            spaceService.addSpace(spaceAddDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(response.getWriter(), new ApiResponse("Space added successfully"));
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(), new ApiResponse("All parameters are required"));
        }
    }

    private boolean isValid(SpaceAddDTO spaceAddDTO) {
        return spaceAddDTO != null
                && spaceAddDTO.login() != null
                && spaceAddDTO.password() != null
                && userService.isAdmin(spaceAddDTO.login())
                && spaceAddDTO.name() != null
                && isValidWorkingHours(spaceAddDTO)
                && spaceAddDTO.numberOfDaysAvailableForBooking() != null;
    }

    private boolean isValidWorkingHours(SpaceAddDTO spaceAddDTO) {
        Integer hourOfBeginningWorkingDay = spaceAddDTO.hourOfBeginningWorkingDay();
        Integer hourOfEndingWorkingDay = spaceAddDTO.hourOfEndingWorkingDay();

        if (hourOfBeginningWorkingDay == null || hourOfEndingWorkingDay == null) {
            return false;
        }

        int minHourOfBeginning = 0;
        int maxHourOfEnding = 24;

        return hourOfBeginningWorkingDay >= minHourOfBeginning
                && hourOfEndingWorkingDay >= hourOfBeginningWorkingDay
                && hourOfEndingWorkingDay <= maxHourOfEnding;
    }

    @LoggingTime
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        try {
            SpaceDeleteDTO spaceDeleteDTO = objectMapper.readValue(request.getInputStream(), SpaceDeleteDTO.class);
            deletingSpace(response, spaceDeleteDTO);
        } catch (VerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch (SpaceNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch (DaoException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        }
    }

    private void deletingSpace(HttpServletResponse response, SpaceDeleteDTO spaceDeleteDTO) throws IOException, SpaceNotFoundException {
        if (spaceDeleteDTO != null
                && spaceDeleteDTO.login() != null
                && spaceDeleteDTO.password() != null
                && spaceDeleteDTO.name() != null) {
            userService.checkCredentials(spaceDeleteDTO.login(), spaceDeleteDTO.password());
            if (userService.isAdmin(spaceDeleteDTO.login())) {
                spaceService.deleteSpace(spaceDeleteDTO.name());
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getWriter(), new ApiResponse("Space deleted successfully"));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                objectMapper.writeValue(response.getWriter(), new ApiResponse("You are not authorized to delete this space"));
            }
        }
    }
}