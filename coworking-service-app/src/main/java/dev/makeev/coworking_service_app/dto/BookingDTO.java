package dev.makeev.coworking_service_app.dto;

public record BookingDTO(Long id,
                         String login,
                         String nameOfBookingSpace,
                         String beginningBookingDate,
                         int beginningBookingHour,
                         String endingBookingDate,
                         int endingBookingHour) {
}
