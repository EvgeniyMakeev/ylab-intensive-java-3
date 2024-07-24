package dev.makeev.coworking_service_app.controller;

import dev.makeev.coworking_service_app.dto.ApiResponse;
import dev.makeev.coworking_service_app.dto.TokenResponse;
import dev.makeev.coworking_service_app.dto.UserRequestDTO;
import dev.makeev.coworking_service_app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing user registration.
 */
@Tag(name = "User", description = "User API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    /**
     * Registers a new user.
     *
     * @param userRequestDTO the user data
     * @return an ApiResponse indicating success or failure
     */
    @Operation(summary = "Registration new user")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registration")
    TokenResponse addUser(@Validated @RequestBody UserRequestDTO userRequestDTO) {
        String token = userService.addUser(userRequestDTO.login(), userRequestDTO.password());
        return new TokenResponse("User added successfully", token);
    }

    @Operation(summary = "Login in")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/login")
    TokenResponse logIn(@Validated @RequestBody UserRequestDTO userRequestDTO) {
        String token = userService.checkCredentials(userRequestDTO.login(), userRequestDTO.password());
        return new TokenResponse("Login success", token);
    }

    @Operation(summary = "Login out")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/logout")
    ApiResponse logOut(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        userService.logOut(token);
        return new ApiResponse("Successfully logged out.");
    }
}
