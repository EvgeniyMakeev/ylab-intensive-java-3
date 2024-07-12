package dev.makeev.coworking_service_app.dto;

import org.springframework.lang.NonNull;

public record BookingAddDTO(@NonNull String login,
                            @NonNull String password,
                            @NonNull String nameOfBookingSpace,
                            @NonNull String beginningBookingDate,
                            @NonNull int beginningBookingHour,
                            @NonNull String endingBookingDate,
                            @NonNull int endingBookingHour) {
}
