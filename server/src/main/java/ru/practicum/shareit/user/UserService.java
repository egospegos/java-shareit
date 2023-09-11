package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto get(long userId);

    UserDto create(UserDto userDto);

    UserDto update(long id, UserDto userDto);

    void delete(long userId);
}
