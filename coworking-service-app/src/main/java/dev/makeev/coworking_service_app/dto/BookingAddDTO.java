package dev.makeev.coworking_service_app.dto;

import org.springframework.lang.NonNull;

public record BookingAddDTO(@NonNull String nameOfBookingSpace,
                            @NonNull String beginningBookingDate,
                            @NonNull int beginningBookingHour,
                            @NonNull String endingBookingDate,
                            @NonNull int endingBookingHour) {

    @Override
    public String toString() {
        return nameOfBookingSpace +
                " from " + beginningBookingHour + ":00 " + beginningBookingDate +
                " to " + endingBookingHour + ":00 " + endingBookingDate;
    }

}
