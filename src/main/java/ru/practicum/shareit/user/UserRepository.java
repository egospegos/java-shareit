package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User findById(long userId);

    User save(User user);

    User update(long id, User user);

    void delete(long userId);

    List<String> findAllEmails();
}
