package dev.makeev.coworking_service_app.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.dao.implementation.LogDaoInBd;
import dev.makeev.coworking_service_app.dao.implementation.UserDAOInBd;
import dev.makeev.coworking_service_app.dto.LogOfUserActionDTO;
import dev.makeev.coworking_service_app.dto.UserRequestDTO;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.exceptions.VerificationException;
import dev.makeev.coworking_service_app.mappers.ApiResponse;
import dev.makeev.coworking_service_app.mappers.LogOfUserActionMapper;
import dev.makeev.coworking_service_app.model.LogOfUserAction;
import dev.makeev.coworking_service_app.service.LogService;
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

@WebServlet("/api/v1/log")
public class LogServlet extends HttpServlet {
    public static final String CONTENT_TYPE = "application/json";

    private LogService logService;
    private UserService userService;
    private ObjectMapper objectMapper;
    private final LogOfUserActionMapper logOfUserActionMapper = LogOfUserActionMapper.INSTANCE;

    @Override
    public void init() {
        ConnectionManager connectionManager = new ConnectionManagerImpl();
        logService = new LogService(new LogDaoInBd(connectionManager));
        userService = new UserService(new UserDAOInBd(connectionManager));
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        try (OutputStream outputStream = response.getOutputStream()) {
            UserRequestDTO userRequestDTO = getUserRequestDTO(request, response);
            if (userRequestDTO != null
                    && userService.isAdmin(userRequestDTO.login())) {
                List<LogOfUserAction> logOfUserActions = logService.getLogs();

                List<LogOfUserActionDTO> logOfUserActionsDTOs = logOfUserActions
                        .stream()
                        .map(logOfUserActionMapper::toLogOfUserActionDTO)
                        .toList();

                objectMapper.writeValue(outputStream, logOfUserActionsDTOs);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                objectMapper.writeValue(response.getWriter(), new ApiResponse("You are not authorized to delete this space"));
            }
        } catch (VerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch(DaoException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        }
    }

    private UserRequestDTO getUserRequestDTO(
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        UserRequestDTO userRequestDTO = objectMapper.readValue(request.getInputStream(), UserRequestDTO.class);
        if (userRequestDTO.login() == null
                || userRequestDTO.login().isEmpty()
                || userRequestDTO.password() == null
                || userRequestDTO.password().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(), new ApiResponse("Both login and password parameters are required"));
            return null;
        }
        return userRequestDTO;
    }
}