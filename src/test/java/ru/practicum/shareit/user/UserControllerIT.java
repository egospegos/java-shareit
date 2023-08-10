package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerIT {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void getAll() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService).getAll();
    }

    @SneakyThrows
    @Test
    void getUserById() {
        long userId = 0L;
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService).get(userId);
    }

    @SneakyThrows
    @Test
    void createUser() {

        UserDto userDtoToCreate = new UserDto();
        userDtoToCreate.setName("name");
        userDtoToCreate.setEmail("mail@mail.ru");
        when(userService.create(userDtoToCreate)).thenReturn(userDtoToCreate);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoToCreate), result);


    }

    @SneakyThrows
    @Test
    void updateUser_whenUserIsNotValid_thenReturnBadRequest() {
        Long userId = 0L;
        UserDto userDtoToUpdate = new UserDto();
        userDtoToUpdate.setEmail("mail");

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToUpdate)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).update(userId, userDtoToUpdate);
    }

    @SneakyThrows
    @Test
    void delete() {
        long userId = 0L;
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).delete(userId);

    }
}