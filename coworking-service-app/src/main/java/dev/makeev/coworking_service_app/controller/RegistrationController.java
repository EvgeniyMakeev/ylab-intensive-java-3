package dev.makeev.coworking_service_app.controller;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dto.ApiResponse;
import dev.makeev.coworking_service_app.dto.UserRequestDTO;
import dev.makeev.coworking_service_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/registration", produces = MediaType.APPLICATION_JSON_VALUE)
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @LoggingTime
    @PostMapping
    public ResponseEntity<ApiResponse> addUser(@Validated @RequestBody UserRequestDTO userRequestDTO) {
        userService.addUser(userRequestDTO.login(), userRequestDTO.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Space added successfully"));
    }
}