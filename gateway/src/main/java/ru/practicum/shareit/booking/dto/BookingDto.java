package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.user.dto.UserForBookingDto;
import ru.practicum.shareit.util.common.Marker;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    private Long id;
    private long itemId;
    @FutureOrPresent(groups = {Marker.OnCreate.class})
    private LocalDateTime start;
    @FutureOrPresent(groups = {Marker.OnCreate.class})
    private LocalDateTime end;

    private Status status;
    private ItemForBookingDto item;
    private UserForBookingDto booker;


}
