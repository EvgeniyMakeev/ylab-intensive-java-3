package dev.makeev.coworking_service_app.mappers;

import dev.makeev.coworking_service_app.dto.BookingAddDTO;
import dev.makeev.coworking_service_app.dto.BookingDTO;
import dev.makeev.coworking_service_app.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "beginningBookingDate", source = "booking.bookingRange.beginningBookingDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "beginningBookingHour", source = "booking.bookingRange.beginningBookingHour")
    @Mapping(target = "endingBookingDate", source = "booking.bookingRange.endingBookingDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "endingBookingHour", source = "booking.bookingRange.endingBookingHour")
    BookingDTO toBookingDTO(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookingRange.beginningBookingDate", dateFormat = "yyyy-MM-dd", source = "bookingAddDTO.beginningBookingDate")
    @Mapping(target = "bookingRange.beginningBookingHour", source = "bookingAddDTO.beginningBookingHour")
    @Mapping(target = "bookingRange.endingBookingDate", dateFormat = "yyyy-MM-dd", source = "bookingAddDTO.endingBookingDate")
    @Mapping(target = "bookingRange.endingBookingHour", source = "bookingAddDTO.endingBookingHour")
    Booking toBooking(String login, BookingAddDTO bookingAddDTO);
}
