package dev.makeev.coworking_service_app.model;

public record User(String login,
                   String password,
                   Boolean isAdmin) {
}