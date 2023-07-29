package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.util.common.Marker;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private int id;
    @NotBlank(groups = {Marker.OnCreate.class})
    private String text;
    private String authorName;
    private LocalDateTime created;
}
