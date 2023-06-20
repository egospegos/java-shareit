package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DublicateException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap();
    private long userId = 1;

    @Override
    public List<User> findAll() {
        List<User> userArrayList = new ArrayList<User>(users.values());
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
        validateWithExceptions(user);
        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь с id = {}", user.getId());
        return user;
    }

    @Override
    public User update(long id, User user) {
        validateWithExceptionsOnUpdate(id, user);
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
        List<String> emailArrayList = new ArrayList<>();
        for (User user : users.values()) {
            emailArrayList.add(user.getEmail());
        }
        return emailArrayList;
    }

    private void validateWithExceptions(User user) {
        if (findAllEmails().contains(user.getEmail())) {
            throw new DublicateException("Ошибка добавления существующего email");
        }
    }

    private void validateWithExceptionsOnUpdate(long id, User user) {
        if (user.getEmail() != null) {
            if (!user.getEmail().equals(findById(id).getEmail())) {
                if (findAllEmails().contains(user.getEmail())) { //если такой майл уже есть в других
                    throw new DublicateException("Ошибка добавления существующего email");
                }
            }
        }
    }
}
