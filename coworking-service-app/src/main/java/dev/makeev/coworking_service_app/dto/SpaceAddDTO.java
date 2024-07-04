package dev.makeev.coworking_service_app.dto;

public record SpaceAddDTO(String login,
                          String password,
                          String name,
                          Integer hourOfBeginningWorkingDay,
                          Integer hourOfEndingWorkingDay,
                          Integer numberOfDaysAvailableForBooking) {
}
