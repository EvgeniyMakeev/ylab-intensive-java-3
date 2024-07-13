package dev.makeev.coworking_service_app.model;

import java.util.List;

public record SlotsAvailableForBooking(String date, List<String> slots) {
}
