package dev.makeev.coworking_service_app.controller;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dto.LogOfUserActionDTO;
import dev.makeev.coworking_service_app.dto.UserRequestDTO;
import dev.makeev.coworking_service_app.exceptions.NoAdminException;
import dev.makeev.coworking_service_app.service.LogService;
import dev.makeev.coworking_service_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing user logs.
 */
@RestController
@RequestMapping(value = "/api/v1/log", produces = MediaType.APPLICATION_JSON_VALUE)
public class LogController {

    private final LogService logService;
    private final UserService userService;

    /**
     * Constructs a LogController with the specified LogService, UserService, and LogOfUserActionMapper.
     *
     * @param logService the log service
     * @param userService the user service
     */
    @Autowired
    public LogController(LogService logService, UserService userService) {
        this.logService = logService;
        this.userService = userService;
    }

    /**
     * Retrieves logs of user actions.
     *
     * @param userRequestDTO the user credentials
     * @return a list of LogOfUserActionDTO
     */
    @LoggingTime
    @PutMapping
    public ResponseEntity<List<LogOfUserActionDTO>> getLog(@Validated @RequestBody UserRequestDTO userRequestDTO) {
        userService.checkCredentials(userRequestDTO.login(), userRequestDTO.password());
        if (userService.isAdmin(userRequestDTO.login())) {
            return ResponseEntity.ok(logService.getLogs());
        } else {
            throw new NoAdminException();
        }
    }
}
