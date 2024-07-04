package dev.makeev.coworking_service_app.mappers;

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
    @Mapping(target = "beginningBookingDate", source = "booking.bookingRange.beginningBookingDate",
            dateFormat = "dd.MM.yyyy")
    @Mapping(target = "beginningBookingHour", source = "booking.bookingRange.beginningBookingHour",
            numberFormat = "0#")
    @Mapping(target = "endingBookingDate", source = "booking.bookingRange.endingBookingDate",
            dateFormat = "dd.MM.yyyy")
    @Mapping(target = "endingBookingHour", source = "booking.bookingRange.endingBookingHour",
            numberFormat = "0#")
    BookingDTO toBookingDTO(Booking booking);


    @Mapping(target = "loginOfUser", source = "bookingDTO.loginOfUser")
    @Mapping(target = "nameOfBookingSpace", source = "bookingDTO.nameOfBookingSpace")
    @Mapping(target = "bookingRange.beginningBookingDate", source = "bookingDTO.beginningBookingDate")
    @Mapping(target = "bookingRange.beginningBookingHour", source = "bookingDTO.beginningBookingHour")
    @Mapping(target = "bookingRange.endingBookingDate", source = "bookingDTO.endingBookingDate")
    @Mapping(target = "bookingRange.endingBookingHour", source = "bookingDTO.endingBookingHour")
    Booking toBooking(BookingDTO bookingDTO);
}
