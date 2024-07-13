package dev.makeev.coworking_service_app.dto;

import dev.makeev.coworking_service_app.model.SlotsAvailableForBooking;

import java.util.List;

public record SpaceDTO(String name, List<SlotsAvailableForBooking> slotsAvailableForBookings) {
}