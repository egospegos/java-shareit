package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    BookingDto addNewBooking(long userId, BookingDto bookingDto);

    BookingDto setApproved(long userId, long bookingId, boolean approved);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getAllByUserId(long userId, String state);

    List<BookingDto> getAllByOwnerId(long ownerId, String state);

}
