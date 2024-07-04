package dev.makeev.coworking_service_app.model;

import java.time.LocalDateTime;

public record LogOfUserAction(LocalDateTime localDateTime,
                              String loginOfUser,
                              String messageAboutAction) {
}
