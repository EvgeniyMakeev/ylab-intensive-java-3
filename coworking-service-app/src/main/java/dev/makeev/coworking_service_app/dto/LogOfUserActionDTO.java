package dev.makeev.coworking_service_app.dto;

public record LogOfUserActionDTO(String localDateTime,
                                 String loginOfUser,
                                 String messageAboutAction) {
}
