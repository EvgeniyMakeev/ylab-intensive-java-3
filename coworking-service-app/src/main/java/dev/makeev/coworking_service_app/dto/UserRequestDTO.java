package dev.makeev.coworking_service_app.dto;

import org.springframework.lang.NonNull;

public record UserRequestDTO(@NonNull String login,
                             @NonNull String password) {
}
