package dev.makeev.coworking_service_app.model;

import java.time.LocalDate;

public record BookingRange(LocalDate dateBookingFrom,
                           int hourBookingFrom,
                           LocalDate dateBookingTo,
                           int hourBookingTo) {
}
