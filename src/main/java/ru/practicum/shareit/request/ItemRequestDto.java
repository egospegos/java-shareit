package ru.practicum.shareit.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.util.common.Marker;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemRequestDto {

    @EqualsAndHashCode.Include
    private Long id;
    @NotBlank(groups = {Marker.OnCreate.class})
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items = new ArrayList<>();
}
