package dev.makeev.coworking_service_app.dto;

import org.springframework.lang.NonNull;

public record SpaceAddDTO(@NonNull String name,
                          @NonNull Integer hourOfBeginningWorkingDay,
                          @NonNull Integer hourOfEndingWorkingDay,
                          @NonNull Integer numberOfDaysAvailableForBooking) {
}
