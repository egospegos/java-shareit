package ru.practicum.shareit.user;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.DublicateException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAll() {
        List<User> usersFromRepo = userRepository.findAll();
        List<UserDto> usersDto = new ArrayList<>();
        //маппинг
        UserMapper mapper = Mappers.getMapper(UserMapper.class);
        for (User user : usersFromRepo) {
            usersDto.add(mapper.userToUserDto(user));
        }
        return usersDto;
    }

    @Override
    public UserDto get(long userId) {
        validateId(userId);
        UserMapper mapper = Mappers.getMapper(UserMapper.class);
        User actual = userRepository.findById(userId);
        return mapper.userToUserDto(userRepository.findById(userId));
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        //маппинг
        UserMapper mapper = Mappers.getMapper(UserMapper.class);
        User user = mapper.userDtoToUser(userDto);
        try {
            return mapper.userToUserDto(userRepository.save(user));
        } catch (ConstraintViolationException e) {
            throw new DublicateException(String.format(
                    "Пользователь с %s уже зарегистрирован", user.getEmail()
            ));
        }

    }

    @Override
    @Transactional
    public UserDto update(long id, UserDto userDto) {
        validateId(id); // проверка, есть ли юзер по такому ид
        userDto.setId(id);
        if (userDto.getEmail() == null) {
            userDto.setEmail(userRepository.findEmailById(id)); // присвоить старое значение, если новое не задано
        }
        if (userDto.getName() == null) {
            userDto.setName(userRepository.findNameById(id));
        }
        //маппинг
        UserMapper mapper = Mappers.getMapper(UserMapper.class);
        User user = mapper.userDtoToUser(userDto);
        validateWithExceptionsOnUpdate(id, user);
        return mapper.userToUserDto(userRepository.save(user));
    }

    @Override
    public void delete(long userId) {
        validateId(userId);
        userRepository.deleteById(userId);
    }


    private void validateId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Wrong user id=" + userId));
    }


    private void validateWithExceptionsOnUpdate(long id, User user) {
        if (!user.getEmail().equals(userRepository.findById(id).getEmail())) {
            if (userRepository.findAllEmails().contains(user.getEmail())) { //если такой майл уже есть в других
                throw new DublicateException("Ошибка добавления существующего email");
            }
        }
    }
}
