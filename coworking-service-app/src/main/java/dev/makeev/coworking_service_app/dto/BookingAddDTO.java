package dev.makeev.coworking_service_app.dto;

public record BookingAddDTO(String loginOfUser,
                            String password,
                            String nameOfBookingSpace,
                            String beginningBookingDate,
                            int beginningBookingHour,
                            String endingBookingDate,
                            int endingBookingHour) {
}
