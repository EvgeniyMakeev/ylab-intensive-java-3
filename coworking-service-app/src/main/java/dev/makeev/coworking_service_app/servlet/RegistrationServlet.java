package dev.makeev.coworking_service_app.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.aop.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dao.implementation.UserDAOInBd;
import dev.makeev.coworking_service_app.dto.UserRequestDTO;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.exceptions.LoginAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.VerificationException;
import dev.makeev.coworking_service_app.mappers.ApiResponse;
import dev.makeev.coworking_service_app.service.UserService;
import dev.makeev.coworking_service_app.util.implementation.ConnectionManagerImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/v1/registration")
public class RegistrationServlet extends HttpServlet {
    public static final String CONTENT_TYPE = "application/json";

    private UserService userService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        userService = new UserService(new UserDAOInBd(new ConnectionManagerImpl()));
        objectMapper = new ObjectMapper();
    }

    @LoggingTime
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        try {
            UserRequestDTO userRequestDTO = getUserRequestDTO(request, response);
            if (userRequestDTO != null) {
                userService.existByLogin(userRequestDTO.login());
                userService.addUser(userRequestDTO);
                response.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(response.getWriter(), new ApiResponse("User added successfully"));
            }
        } catch (LoginAlreadyExistsException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch (DaoException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        }
    }

    @LoggingTime
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        try {
            UserRequestDTO userRequestDTO = getUserRequestDTO(request, response);
            if (userRequestDTO != null) {
                userService.checkCredentials(userRequestDTO.login(), userRequestDTO.password());
                objectMapper.writeValue(response.getWriter(), new ApiResponse("User credentials verified successfully"));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (VerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch (DaoException e) {
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