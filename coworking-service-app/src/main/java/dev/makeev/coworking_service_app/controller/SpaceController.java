package dev.makeev.coworking_service_app.controller;

import dev.makeev.coworking_service_app.dto.ApiResponse;
import dev.makeev.coworking_service_app.dto.SpaceAddDTO;
import dev.makeev.coworking_service_app.dto.SpaceDTO;
import dev.makeev.coworking_service_app.dto.SpaceDeleteDTO;
import dev.makeev.coworking_service_app.exceptions.NoAdminException;
import dev.makeev.coworking_service_app.service.SpaceService;
import dev.makeev.coworking_service_app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
@SecurityRequirement(name = "apiKeyScheme")
@Tag(name = "Spaces", description = "Spaces API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/spaces", produces = MediaType.APPLICATION_JSON_VALUE)
public class SpaceController {

    private final SpaceService spaceService;
    private final UserService userService;

    /**
     * Retrieves all spaces.
     *
     * @return a list of SpaceDTO
     */
    @Operation(summary = "Get all Spaces", description = "Spaces with free slots")
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
    @Operation(summary = "Add new Space", description = "Available only for Admin")
    @PostMapping
    public ResponseEntity<ApiResponse> addSpace(HttpServletRequest request,
                                                @Validated @RequestBody SpaceAddDTO spaceAddDTO) {
        String login = (String) request.getAttribute("login");
        if (isValid(login, spaceAddDTO)) {
            spaceService.addSpace(spaceAddDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Space " + spaceAddDTO.name() + " added successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("All parameters are required"));
        }
    }

    /**
     * Validates the space data.
     *
     * @param spaceAddDTO the space data
     * @return true if valid, false otherwise
     */
    private boolean isValid(String login, SpaceAddDTO spaceAddDTO) {
        if (userService.isAdmin(login)) {
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
    @Operation(summary = "Space bookings by Name", description = "Available only for Admin")
    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteSpace(HttpServletRequest request,
                                                   @Validated @RequestBody SpaceDeleteDTO spaceDeleteDTO) {
        String login = (String) request.getAttribute("login");
        if (userService.isAdmin(login)) {
            spaceService.deleteSpace(spaceDeleteDTO.name());
        } else {
            throw new NoAdminException();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new ApiResponse("Space deleted successfully"));
    }
}
