package dev.makeev.coworking_service_app.model;

import java.time.LocalDate;
import java.util.Map;

public record Space(String name,
                    WorkingHours workingHours,
                    Map<LocalDate, Map<Integer, Boolean>> bookingSlots) {
}
