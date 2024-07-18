package dev.makeev.coworking_service_app.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.dto.ApiResponse;
import dev.makeev.coworking_service_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthFilter implements Filter {

    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

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
