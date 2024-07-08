package dev.makeev.coworking_service_app.dto;

import java.util.List;

public record SlotsAvailableForBookingDTO(String date, List<String> slots) {
}
