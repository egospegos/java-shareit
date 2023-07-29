package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.ItemForBookingDto;
import ru.practicum.shareit.user.UserForBookingDto;
import ru.practicum.shareit.util.common.Marker;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
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
