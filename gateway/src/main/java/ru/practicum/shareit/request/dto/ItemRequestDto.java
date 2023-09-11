package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.common.Marker;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {

    private Long id;
    @NotBlank(groups = {Marker.OnCreate.class})
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items = new ArrayList<>();
}
