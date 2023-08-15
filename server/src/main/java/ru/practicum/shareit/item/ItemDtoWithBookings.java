package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.booking.BookingShort;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDtoWithBookings {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingShort lastBooking;
    private BookingShort nextBooking;

    private List<CommentDto> comments = new ArrayList<>();
}
