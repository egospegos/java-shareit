package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final HashMap<Long, User> users = new HashMap();
    private long userId = 1;

    @Override
    public List<User> findAll() {
        ArrayList<User> userArrayList = new ArrayList<User>(users.values());
        log.info("Количество пользователей в списке = {}", userArrayList.size());
        return userArrayList;
    }

    @Override
    public User findById(long userId) {
        return users.get(userId);
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }

    @Override
    public User save(User user) {
        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь с id = {}", user.getId());
        return user;
    }

    @Override
    public User update(long id, User user) {
        if (!users.containsKey(id)) {
            throw new ValidationException("Пользователь по ключу с id: " + id + " не найден");
        }
        user.setId(id);
        if (user.getEmail() == null) {
            user.setEmail(users.get(id).getEmail()); // присвоить старое значение, если новое не задано
        }
        if (user.getName() == null) {
            user.setName(users.get(id).getName());
        }
        users.put(id, user);
        log.info("Изменён пользователь с id = {}", user.getId());
        return user;
    }

    @Override
    public List<String> findAllEmails() {
        ArrayList<String> emailArrayList = new ArrayList<>();
        for (User user : users.values()) {
            emailArrayList.add(user.getEmail());
        }
        return emailArrayList;
    }
}
