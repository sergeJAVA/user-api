package com.example.user_api.controller;

import com.example.user_api.config.TestSecurityConfig;
import com.example.user_api.model.dto.UserDto;
import com.example.user_api.model.entity.User;
import com.example.user_api.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testHome() throws Exception {
        mockMvc.perform(get("/user/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        List<User> users = Arrays.asList(
                new User(1L, "Serega", "encodedPass", "user"),
                new User(2L, "Kolya", "encodedPass", "user")
        );
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/user/all-users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Serega"))
                .andExpect(jsonPath("$[1].name").value("Kolya"));
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User(1L, "Serega", "encodedPass", "user");
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/user/findId/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Serega"));
    }

    @Test
    void testGetUserByName() throws Exception {
        User user = new User(1L, "Serega", "encoded_pass", "user");
        when(userService.findByName("Serega")).thenReturn(user);

        mockMvc.perform(get("/user/find/Serega"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Serega"));
    }

    @Test
    void testCreateUser() throws Exception {
        UserDto userDto = new UserDto("NewUser", "password", "user");
        User user = new User(1L, "NewUser", "encoded_pass", "user");
        when(userService.save(any(UserDto.class))).thenReturn(user);

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewUser"));
    }

    @Test
    void testUpdateUsername() throws Exception {
        when(userService.update("NewName", 1L))
                .thenReturn(ResponseEntity.ok("User updated"));

        mockMvc.perform(post("/user/update-username")
                        .param("id", "1")
                        .param("username", "NewName"))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated"));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(passwordEncoder.encode("newpass")).thenReturn("encoded_newpass");
        when(userService.update("NewName", "encoded_newpass", 1L))
                .thenReturn(ResponseEntity.ok("User updated"));

        mockMvc.perform(post("/user/update")
                        .param("id", "1")
                        .param("username", "NewName")
                        .param("password", "newpass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User updated"));
    }

    @Test
    void testDeleteUser() throws Exception {
        when(userService.deleteById(1L))
                .thenReturn(ResponseEntity.ok("User deleted"));

        mockMvc.perform(delete("/user/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User deleted"));
    }
}