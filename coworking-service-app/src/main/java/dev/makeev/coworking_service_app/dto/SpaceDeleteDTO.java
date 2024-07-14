package dev.makeev.coworking_service_app.dto;

import org.springframework.lang.NonNull;

public record SpaceDeleteDTO(@NonNull String login,
                             @NonNull String password,
                             @NonNull String name) {
}
