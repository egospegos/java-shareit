package ru.practicum.shareit.user;

import lombok.Data;
import ru.practicum.shareit.util.common.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    private Long id;
    @NotBlank(groups = {Marker.OnCreate.class})
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String email;
    @NotBlank(groups = {Marker.OnCreate.class})
    private String name;
}
