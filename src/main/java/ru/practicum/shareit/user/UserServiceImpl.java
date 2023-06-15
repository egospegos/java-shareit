package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.DublicateException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User get(long userId) {
        validateId(userId);
        return userRepository.findById(userId);
    }

    @Override
    public User create(User user) {
        validateWithExceptions(user);
        return userRepository.save(user);
    }

    @Override
    public User update(long id, User user) {
        validateId(id);
        validateWithExceptionsOnUpdate(id, user);
        return userRepository.update(id, user);
    }

    @Override
    public void delete(long userId) {
        validateId(userId);
        userRepository.delete(userId);
    }

    private void validateWithExceptions(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Ошибка валидации пользователя по email");
        }
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Ошибка валидации пользователя по email");
        }
        if (userRepository.findAllEmails().contains(user.getEmail())) {
            throw new DublicateException("Ошибка добавления существующего email");
        }
    }

    private void validateWithExceptionsOnUpdate(long id, User user) {
        if (user.getEmail() != null) {
            if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
                throw new ValidationException("Ошибка валидации пользователя по email");
            }
        }
        if (user.getEmail() != null) {
            if (!user.getEmail().equals(userRepository.findById(id).getEmail())) {
                if (userRepository.findAllEmails().contains(user.getEmail())) { //если такой майл уже есть в других
                    throw new DublicateException("Ошибка добавления существующего email");
                }
            }
        }

    }

    private void validateId(Long id) {
        if (userRepository.findById(id) == null || id < 0) {
            throw new DataNotFoundException("Пользователь с таким id не найден");
        }

    }
}
