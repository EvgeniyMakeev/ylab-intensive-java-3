package dev.makeev.coworking_service_app.controller;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dto.ApiResponse;
import dev.makeev.coworking_service_app.dto.SpaceAddDTO;
import dev.makeev.coworking_service_app.dto.SpaceDTO;
import dev.makeev.coworking_service_app.dto.SpaceDeleteDTO;
import dev.makeev.coworking_service_app.exceptions.NoAdminException;
import dev.makeev.coworking_service_app.service.SpaceService;
import dev.makeev.coworking_service_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing spaces.
 */
@RestController
@RequestMapping(value = "/api/v1/spaces", produces = MediaType.APPLICATION_JSON_VALUE)
public class SpaceController {

    private final SpaceService spaceService;
    private final UserService userService;

    /**
     * Constructs a SpaceController with the specified SpaceService and UserService.
     *
     * @param spaceService the space service
     * @param userService the user service
     */
    @Autowired
    public SpaceController(SpaceService spaceService, UserService userService) {
        this.spaceService = spaceService;
        this.userService = userService;
    }

    /**
     * Retrieves all spaces.
     *
     * @return a list of SpaceDTO
     */
    @LoggingTime
    @GetMapping
    public ResponseEntity<List<SpaceDTO>> getSpaces() {
        return ResponseEntity.ok(spaceService.getSpaces());
    }

    /**
     * Adds a new space.
     *
     * @param spaceAddDTO the space data
     * @return an ApiResponse indicating success or failure
     */
    @LoggingTime
    @PostMapping
    public ResponseEntity<ApiResponse> addSpace(@Validated @RequestBody SpaceAddDTO spaceAddDTO) {
        if (isValid(spaceAddDTO)) {
            userService.checkCredentials(spaceAddDTO.login(), spaceAddDTO.password());
            spaceService.addSpace(spaceAddDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Space added successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("All parameters are required"));
        }
    }

    /**
     * Validates the space data.
     *
     * @param spaceAddDTO the space data
     * @return true if valid, false otherwise
     */
    private boolean isValid(SpaceAddDTO spaceAddDTO) {
        if (userService.isAdmin(spaceAddDTO.login())) {
            int minHourOfBeginning = 0;
            int maxHourOfEnding = 24;

            return spaceAddDTO.hourOfBeginningWorkingDay() >= minHourOfBeginning
                    && spaceAddDTO.hourOfEndingWorkingDay() >= spaceAddDTO.hourOfBeginningWorkingDay()
                    && spaceAddDTO.hourOfEndingWorkingDay() <= maxHourOfEnding;
        } else {
            throw new NoAdminException();
        }
    }

    /**
     * Deletes a space.
     *
     * @param spaceDeleteDTO the space data
     * @return an ApiResponse indicating success or failure
     */
    @LoggingTime
    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteSpace(@Validated @RequestBody SpaceDeleteDTO spaceDeleteDTO) {
        userService.checkCredentials(spaceDeleteDTO.login(), spaceDeleteDTO.password());
        if (userService.isAdmin(spaceDeleteDTO.login())) {
            spaceService.deleteSpace(spaceDeleteDTO.name());
        } else {
            throw new NoAdminException();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse("Space deleted successfully"));
    }
}
