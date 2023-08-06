package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BookingMapper {
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "start", source = "entity.start")
    @Mapping(target = "end", source = "entity.end")
    @Mapping(target = "status", source = "entity.status")
    Booking bookingDtoToBooking(BookingDto entity);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "start", source = "entity.start")
    @Mapping(target = "end", source = "entity.end")
    @Mapping(target = "status", source = "entity.status")
    BookingDto bookingToBookingDto(Booking entity);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "bookerId", expression = "java(entity.getBooker().getId())")
    BookingShort bookingToBookingShort(Booking entity);


}
