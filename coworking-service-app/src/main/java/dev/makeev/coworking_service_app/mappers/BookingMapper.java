package dev.makeev.coworking_service_app.mappers;

import dev.makeev.coworking_service_app.dto.BookingDTO;
import dev.makeev.coworking_service_app.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "loginOfUser", source = "booking.loginOfUser")
    @Mapping(target = "nameOfBookingSpace", source = "booking.nameOfBookingSpace")
    @Mapping(target = "beginningBookingDate", source = "booking.bookingRange.beginningBookingDate", qualifiedByName = "localDateToString")
    @Mapping(target = "beginningBookingHour", source = "booking.bookingRange.beginningBookingHour")
    @Mapping(target = "endingBookingDate", source = "booking.bookingRange.endingBookingDate", qualifiedByName = "localDateToString")
    @Mapping(target = "endingBookingHour", source = "booking.bookingRange.endingBookingHour")
    BookingDTO toBookingDTO(Booking booking);

//    @Mapping(target = "id", source = "")
//    @Mapping(target = "", source = "bookingAddDTO.password")
//    @Mapping(target = "loginOfUser", source = "bookingAddDTO.loginOfUser")
//    @Mapping(target = "nameOfBookingSpace", source = "bookingAddDTO.nameOfBookingSpace")
//    @Mapping(target = "bookingRange.beginningBookingDate", source = "beginningBookingDate", qualifiedByName = "stringToLocalDate")
//    @Mapping(target = "bookingRange.beginningBookingHour", source = "bookingAddDTO.beginningBookingHour")
//    @Mapping(target = "bookingRange.endingBookingDate", source = "endingBookingDate", qualifiedByName = "stringToLocalDate")
//    @Mapping(target = "bookingRange.endingBookingHour", source = "bookingAddDTO.endingBookingHour")
//    Booking toBooking(BookingAddDTO bookingAddDTO);

    @Named("localDateToString")
    static String localDateToString(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Named("stringToLocalDate")
    static LocalDate stringToLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
