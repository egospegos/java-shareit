package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.util.common.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ItemDto {
    private Long id;
    @NotBlank(groups = {Marker.OnCreate.class})
    private String name;
    @NotBlank(groups = {Marker.OnCreate.class})
    private String description;
    @NotNull(groups = {Marker.OnCreate.class})
    private Boolean available;
}
