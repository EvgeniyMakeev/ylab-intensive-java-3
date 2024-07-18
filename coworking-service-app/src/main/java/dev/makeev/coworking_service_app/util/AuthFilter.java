package dev.makeev.coworking_service_app.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.dto.ApiResponse;
import dev.makeev.coworking_service_app.service.UserService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthFilter implements Filter {

    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        if (path.startsWith("/api/v1/user")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || authHeader.isEmpty()) {
            getErrorMassage(httpResponse,"Authorization header is missing");
            return;
        }

        try {
            String login = userService.validateToken(authHeader);
            httpRequest.setAttribute("login", login);
            chain.doFilter(request, response);
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            getErrorMassage(httpResponse,"Invalid token");
        }
    }

    private void getErrorMassage(HttpServletResponse httpResponse, String message) throws IOException {
        httpResponse.setContentType("application/json");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        objectMapper.writeValue(httpResponse.getWriter(), new ApiResponse(message));
    }
}
