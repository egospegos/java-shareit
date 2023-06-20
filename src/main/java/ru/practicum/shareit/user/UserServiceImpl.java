package ru.practicum.shareit.user;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
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
        return mapper.userToUserDto(userRepository.findById(userId));
    }

    @Override
    public User create(UserDto userDto) {
        //маппинг
        UserMapper mapper = Mappers.getMapper(UserMapper.class);
        User user = mapper.userDtoToUser(userDto);
        return userRepository.save(user);
    }

    @Override
    public User update(long id, UserDto userDto) {
        //маппинг
        UserMapper mapper = Mappers.getMapper(UserMapper.class);
        User user = mapper.userDtoToUser(userDto);
        validateId(id);
        return userRepository.update(id, user);
    }

    @Override
    public void delete(long userId) {
        validateId(userId);
        userRepository.delete(userId);
    }


    private void validateId(Long id) {
        if (userRepository.findById(id) == null || id < 0) {
            throw new DataNotFoundException("Пользователь с таким id не найден");
        }
    }
}
