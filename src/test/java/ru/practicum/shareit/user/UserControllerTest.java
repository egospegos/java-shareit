package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {


    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Test
    void getAllUsers() {
        List<UserDto> expectedUsers = new ArrayList<>();
        expectedUsers.add(new UserDto());

        Mockito.when(userService.getAll()).thenReturn(expectedUsers);
        List<UserDto> usersDto = userController.getAll();

        assertEquals(expectedUsers, usersDto);
    }

    @Test
    void getUserById() {
        UserDto expectedUser = new UserDto();

        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(expectedUser);
        UserDto userDto = userController.get(1);

        assertEquals(expectedUser, userDto);
    }

    @Test
    void createUser() {
        UserDto expectedUser = new UserDto();

        Mockito.when(userService.create(expectedUser)).thenReturn(expectedUser);
        UserDto userDto = userController.create(expectedUser);

        assertEquals(expectedUser, userDto);
        Mockito.verify(userService).create(expectedUser);
    }

    @Test
    void updateUser() {
        UserDto expectedUser = new UserDto();

        Mockito.when(userService.update(1, expectedUser)).thenReturn(expectedUser);
        UserDto userDto = userController.update(1, expectedUser);

        assertEquals(expectedUser, userDto);
        Mockito.verify(userService).update(1, expectedUser);
    }

    @Test
    void deleteUser() {
        userController.delete(1);
        Mockito.verify(userService).delete(1);
    }


}