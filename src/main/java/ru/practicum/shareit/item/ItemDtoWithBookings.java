package ru.practicum.shareit.item;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingShort;
import ru.practicum.shareit.util.common.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class ItemDtoWithBookings {
    private Long id;
    @NotBlank(groups = {Marker.OnCreate.class})
    private String name;
    @NotBlank(groups = {Marker.OnCreate.class})
    private String description;
    @NotNull(groups = {Marker.OnCreate.class})
    private Boolean available;

    private BookingShort lastBooking;
    private BookingShort nextBooking;

    private List<CommentDto> comments = new ArrayList<>();
}
