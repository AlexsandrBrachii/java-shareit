package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    UserDto user;

    @BeforeEach
    void setUp() {
        user = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@example.com")
                .build();
    }

    @Test
    void createUser_Normal_Return201() throws Exception {
        Mockito.when(userService.createUser(user)).thenReturn(user);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void createUser_EmptyName_ReturnBadRequest() throws Exception {
        user.setName("");

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WrongEmail_ReturnBadRequest() throws Exception {
        user.setEmail("example.com");

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_Normal() throws Exception {
        user.setEmail("user@example.com");
        user.setName("updatedUser");

        UserDto userUpdated = UserDto.builder()
                .id(1L)
                .name("updatedUser")
                .email("user@example.com")
                .build();

        Mockito.when(userService.updateUser(1L, user))
                .thenReturn(userUpdated);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("updatedUser"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void get_FindById_Noraml() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void delete_Normal() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    void get_FindAll_Normal() throws Exception {
        UserDto user1 = UserDto.builder()
                .id(1L)
                .name("user1")
                .email("user1@example.com")
                .build();

        UserDto user2 = UserDto.builder()
                .id(2L)
                .name("user2")
                .email("user2@example.com")
                .build();

        List<UserDto> userList = List.of(user1, user2);

        Mockito.when(userService.getAllUsers()).thenReturn(userList);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(List.of(user1, user2))));
    }
}
