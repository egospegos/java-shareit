package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    Booking addNewBooking(long userId, BookingDto bookingDto);

    Booking setApproved(long userId, long bookingId, boolean approved);

    Booking getBooking(long userId, long bookingId);

    List<Booking> getAllByUserId(long userId, String state);

    List<Booking> getAllByOwnerId(long ownerId, String state);

}
