package dev.makeev.coworking_service_app.mappers;

import dev.makeev.coworking_service_app.dto.BookingAddDTO;
import dev.makeev.coworking_service_app.dto.BookingDTO;
import dev.makeev.coworking_service_app.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "loginOfUser", source = "booking.loginOfUser")
    @Mapping(target = "nameOfBookingSpace", source = "booking.nameOfBookingSpace")
    @Mapping(target = "beginningBookingDate", source = "booking.bookingRange.beginningBookingDate", dateFormat = "dd.MM.yyyy")
    @Mapping(target = "beginningBookingHour", source = "booking.bookingRange.beginningBookingHour")
    @Mapping(target = "endingBookingDate", source = "booking.bookingRange.endingBookingDate", dateFormat = "dd.MM.yyyy")
    @Mapping(target = "endingBookingHour", source = "booking.bookingRange.endingBookingHour")
    BookingDTO toBookingDTO(Booking booking);

    @Mapping(target = "id", ignore = true, source = "password")
    @Mapping(target = "loginOfUser", source = "bookingAddDTO.loginOfUser")
    @Mapping(target = "nameOfBookingSpace", source = "bookingAddDTO.nameOfBookingSpace")
    @Mapping(target = "bookingRange.beginningBookingDate", source = "beginningBookingDate", dateFormat = "dd.MM.yyyy")
    @Mapping(target = "bookingRange.beginningBookingHour", source = "bookingAddDTO.beginningBookingHour")
    @Mapping(target = "bookingRange.endingBookingDate", source = "endingBookingDate", dateFormat = "dd.MM.yyyy")
    @Mapping(target = "bookingRange.endingBookingHour", source = "bookingAddDTO.endingBookingHour")
    Booking toBooking(BookingAddDTO bookingAddDTO);
}
