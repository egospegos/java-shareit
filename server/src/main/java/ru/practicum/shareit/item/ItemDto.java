package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.util.common.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {
    private Long id;
    @NotBlank(groups = {Marker.OnCreate.class})
    private String name;
    @NotBlank(groups = {Marker.OnCreate.class})
    private String description;
    @NotNull(groups = {Marker.OnCreate.class})
    private Boolean available;

    private long requestId;
}
