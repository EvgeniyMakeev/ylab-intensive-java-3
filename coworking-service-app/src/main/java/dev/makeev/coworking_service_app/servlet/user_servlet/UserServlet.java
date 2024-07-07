package dev.makeev.coworking_service_app.servlet.user_servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.aop.annotations.Loggable;
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
import java.io.Writer;

@Loggable
@WebServlet("/api/v1/login")
public class UserServlet extends HttpServlet {
    public static final String CONTENT_TYPE = "application/json";

    private UserService userService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        userService = new UserService(new UserDAOInBd(new ConnectionManagerImpl()));
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserRequestDTO userRequestDTO = getUserRequestDTO(req, resp);
            if (userRequestDTO != null) {
                userService.existByLogin(userRequestDTO.login());
                userService.addUser(userRequestDTO.login(), userRequestDTO.password());
                resp.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(resp.getWriter(), new ApiResponse("User added successfully"));
            }
        } catch (LoginAlreadyExistsException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            objectMapper.writeValue(resp.getWriter(), new ApiResponse(e.getMessage()));
        } catch (DaoException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ApiResponse(e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            UserRequestDTO userRequestDTO = getUserRequestDTO(request, response);
            if (userRequestDTO != null) {
                userService.checkCredentials(userRequestDTO.login(), userRequestDTO.password());
                response.setStatus(HttpServletResponse.SC_OK);
                Writer out = response.getWriter();
                objectMapper.writeValue(out, new ApiResponse("User credentials verified successfully"));
                out.close();
            }
        } catch (VerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));
        } catch (DaoException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), new ApiResponse(e.getMessage()));

        }
    }

    private UserRequestDTO getUserRequestDTO(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserRequestDTO userRequestDTO = objectMapper.readValue(req.getInputStream(), UserRequestDTO.class);
        resp.setContentType(CONTENT_TYPE);
        if (userRequestDTO.login() == null
                || userRequestDTO.login().isEmpty()
                || userRequestDTO.password() == null
                || userRequestDTO.password().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ApiResponse("Both login and password parameters are required"));
            return null;
        }
        return userRequestDTO;
    }
}