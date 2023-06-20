package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto get(long userId);

    User create(UserDto userDto);

    User update(long id, UserDto userDto);

    void delete(long userId);
}
