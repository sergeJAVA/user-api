package com.example.user_api.service;

import com.example.user_api.mapper.UserMapper;
import com.example.user_api.model.dto.UserDto;
import com.example.user_api.model.entity.User;
import com.example.user_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private User testUser;
    private UserDto testUserDto;


    @BeforeEach
    void setUp() {
        testUser = User.builder().name("testUser").password("password").id(1L).build();
        testUserDto = UserDto.builder().name("testUser").password("password").build();
    }

    @Test
    void findAll_ShouldReturnListOfUsers() {
        List<User> users = Arrays.asList(testUser);
        when(userService.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertEquals(users, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(testUser));

        User result = userService.findById(1L);

        assertEquals(testUser, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findById_WhenUserNotExists_ShouldReturnNull() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User result = userService.findById(1L);

        assertNull(result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findByName_WhenUserNotExists_ShouldReturnNull() {
        when(userRepository.findByName("testUser")).thenReturn(Optional.empty());

        User result = userService.findByName("testUser");

        assertNull(result);
        verify(userRepository, times(1)).findByName("testUser");
    }


    @Test
    void findByName_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByName("testUser")).thenReturn(Optional.ofNullable(testUser));

        User result = userService.findByName("testUser");

        assertEquals(testUser, result);
        verify(userRepository, times(1)).findByName("testUser");
    }

    @Test
    void save_ShouldReturnSavedUser() {
        when(userMapper.toUser(testUserDto)).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.save(testUserDto);

        assertEquals(testUser, result);
        verify(userMapper, times(1)).toUser(testUserDto);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateUsername_WhenUserExists_ShouldReturnSuccessResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        ResponseEntity<String> result = userService.update("newUsername", 1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("The username with id 1 has been changed", result.getBody());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateUsername_WhenUserNotExists_ShouldReturnBadRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> result = userService.update("newUsername", 1L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("The user with ID 1 doesn't exist ", result.getBody());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void updateUsernameAndPassword_WhenUserExists_ShouldReturnSuccessResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        ResponseEntity<String> result = userService.update("newUsername", "newPassword", 1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Username and password on id 1 have been changed", result.getBody());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete() {
        User user = new User(1L, "Serega", "password", "user");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<String> response = userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The user with 1 has been deleted", response.getBody());
    }

    @Test
    void saveAll_ShouldCallRepositorySaveAll() {
        List<User> users = Arrays.asList(testUser);

        userService.saveAll(users);

        verify(userRepository, times(1)).saveAll(users);
    }
}
