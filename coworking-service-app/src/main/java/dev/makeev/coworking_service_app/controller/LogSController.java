package dev.makeev.coworking_service_app.controller;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dto.LogOfUserActionDTO;
import dev.makeev.coworking_service_app.dto.UserRequestDTO;
import dev.makeev.coworking_service_app.exceptions.NoAdminException;
import dev.makeev.coworking_service_app.mappers.LogOfUserActionMapper;
import dev.makeev.coworking_service_app.model.LogOfUserAction;
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

@RestController
@RequestMapping(value = "/api/v1/log", produces = MediaType.APPLICATION_JSON_VALUE)
public class LogSController {

    private final LogService logService;
    private final UserService userService;
    private final LogOfUserActionMapper logOfUserActionMapper;

    @Autowired
    public LogSController(LogService logService, UserService userService, LogOfUserActionMapper logOfUserActionMapper) {
        this.logService = logService;
        this.userService = userService;
        this.logOfUserActionMapper = logOfUserActionMapper;
    }

    @LoggingTime
    @PutMapping
    public ResponseEntity<List<LogOfUserActionDTO>> getLog(@Validated @RequestBody UserRequestDTO userRequestDTO) {
        userService.checkCredentials(userRequestDTO.login(), userRequestDTO.password());
        if (userService.isAdmin(userRequestDTO.login())) {
            List<LogOfUserAction> logOfUserActions = logService.getLogs();

            List<LogOfUserActionDTO> logOfUserActionsDTOs = logOfUserActions
                    .stream()
                    .map(logOfUserActionMapper::toLogOfUserActionDTO)
                    .toList();
            return ResponseEntity.ok(logOfUserActionsDTOs);
        } else {
            throw new NoAdminException();
        }
    }
}