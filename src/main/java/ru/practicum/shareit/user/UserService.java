package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User get(long userId);

    User create(User user);

    User update(long id, User user);

    void delete(long userId);
}
