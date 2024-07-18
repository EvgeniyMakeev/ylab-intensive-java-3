package dev.makeev.coworking_service_app.controller;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dto.LogOfUserActionDTO;
import dev.makeev.coworking_service_app.exceptions.NoAdminException;
import dev.makeev.coworking_service_app.service.LogService;
import dev.makeev.coworking_service_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * REST controller for managing user logs.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/log", produces = MediaType.APPLICATION_JSON_VALUE)
public class LogController {

    private final LogService logService;
    private final UserService userService;

    /**
     * Retrieves logs of user actions.
     *
     * @return a list of LogOfUserActionDTO
     */
    @LoggingTime
    @GetMapping
    public ResponseEntity<List<LogOfUserActionDTO>> getLog(HttpServletRequest request) {
        String login = (String) request.getAttribute("login");
        if (userService.isAdmin(login)) {
            return ResponseEntity.ok(logService.getLogs());
        } else {
            throw new NoAdminException();
        }
    }
}
