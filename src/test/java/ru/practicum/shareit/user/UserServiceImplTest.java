package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.DublicateException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Test
    void getAll() {
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User());
        expectedUsers.add(new User());

        Mockito.when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> actualUsersDto = userServiceImpl.getAll();
        List<UserDto> expectedUsersDto = new ArrayList<>();
        for (User user : expectedUsers) {
            expectedUsersDto.add(Mappers.getMapper(UserMapper.class).userToUserDto(user));
        }

        assertEquals(expectedUsersDto, actualUsersDto);
        assertEquals(2, actualUsersDto.size());


    }

    @Test
    void getUser() {
        Long userId = 1L;
        User expectedUser = new User();

        Mockito.when(userRepository.findById(userId.longValue())).thenReturn(expectedUser);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        UserDto actualUserDto = userServiceImpl.get(userId);
        UserDto expectedUserDto = Mappers.getMapper(UserMapper.class).userToUserDto(expectedUser);

        assertEquals(expectedUserDto, actualUserDto);

    }

    @Test
    void getUserWithException_DataNotFound() {
        Long userId = 1L;

        Mockito.when(userRepository.findById(userId)).thenThrow(DataNotFoundException.class);

        assertThrows(DataNotFoundException.class,
                () -> userServiceImpl.get(userId));


    }

    @Test
    void getUserWithException_IncorrectUserId() {
        Long userId = -1L;
        User expectedUser = new User();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        assertThrows(DataNotFoundException.class,
                () -> userServiceImpl.get(userId));

    }

    @Test
    void create() {
        long id = 1L;
        User expectedUser = new User();
        expectedUser.setEmail("mail@mail.ru");
        expectedUser.setName("name");
        Mockito.when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        UserDto expectedUserDto = Mappers.getMapper(UserMapper.class).userToUserDto(expectedUser);
        UserDto actualUserDto = userServiceImpl.create(expectedUserDto);

        assertEquals(expectedUserDto, actualUserDto);
        Mockito.verify(userRepository).save(expectedUser);

    }

    @Test
    void create_ThrowDublicateException() {
        User expectedUser = new User();
        expectedUser.setEmail("mail.ru");
        expectedUser.setName("name");
        Mockito.when(userRepository.save(expectedUser)).thenThrow(ConstraintViolationException.class);

        UserDto expectedUserDto = Mappers.getMapper(UserMapper.class).userToUserDto(expectedUser);

        assertThrows(DublicateException.class,
                () -> userServiceImpl.create(expectedUserDto));

    }

    @Test
    void updateUser_NewName() {
        Long id = 1L;
        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(id);
        expectedUserDto.setName("nameNew");
        User expectedUser = Mappers.getMapper(UserMapper.class).userDtoToUser(expectedUserDto);
        expectedUser.setEmail("mail@mail.ru");

        Mockito.when(userRepository.findEmailById(id)).thenReturn("mail@mail.ru");
        Mockito.when(userRepository.save(expectedUser)).thenReturn(expectedUser);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));
        Mockito.when(userRepository.findById(id.longValue())).thenReturn(expectedUser);

        UserDto actualUserDto = userServiceImpl.update(id, expectedUserDto);
        expectedUserDto.setEmail("mail@mail.ru");
        assertEquals(expectedUserDto, actualUserDto);

    }

    @Test
    void updateUser_NewEmail() {
        Long id = 1L;
        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(id);
        expectedUserDto.setEmail("mailNew@mail.ru");
        User expectedUser = Mappers.getMapper(UserMapper.class).userDtoToUser(expectedUserDto);
        expectedUser.setName("name");

        Mockito.when(userRepository.findNameById(id)).thenReturn("name");
        Mockito.when(userRepository.save(expectedUser)).thenReturn(expectedUser);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));
        Mockito.when(userRepository.findById(id.longValue())).thenReturn(expectedUser);

        UserDto actualUserDto = userServiceImpl.update(id, expectedUserDto);
        expectedUserDto.setName("name");
        assertEquals(expectedUserDto, actualUserDto);

    }

    @Test
    void updateUser_ExistingEmail() {
        Long id = 1L;
        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(id);
        expectedUserDto.setEmail("mail@mail.ru");
        User expectedUser = Mappers.getMapper(UserMapper.class).userDtoToUser(expectedUserDto);
        expectedUser.setName("name");

        User anotherUser = new User();
        anotherUser.setEmail("another@mail.ru");
        List<String> mails = new ArrayList<>();
        mails.add("mail@mail.ru");

        Mockito.when(userRepository.findNameById(id)).thenReturn("name");
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));
        Mockito.when(userRepository.findById(id.longValue())).thenReturn(anotherUser);
        Mockito.when(userRepository.findAllEmails()).thenReturn(mails);

        assertThrows(DublicateException.class,
                () -> userServiceImpl.update(id, expectedUserDto));

    }

    @Test
    void delete() {
        Long id = 1L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(new User()));
        userServiceImpl.delete(id);

        Mockito.verify(userRepository).deleteById(id.longValue());
    }
}