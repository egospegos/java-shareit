package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.ItemForBookingDto;
import ru.practicum.shareit.user.UserForBookingDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    private Long id;
    private long itemId;

    private LocalDateTime start;

    private LocalDateTime end;

    private Status status;
    private ItemForBookingDto item;
    private UserForBookingDto booker;


}
