package com.example.user_api.service;

import com.example.user_api.model.entity.User;
import com.example.user_api.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void findByNameShouldReturnNull() {
        Mockito.when(userRepository.findByName("Ivan")).thenReturn(Optional.empty());

        User ivan = userService.findByName("Ivan");
        Assertions.assertNull(ivan);
    }


    @Test
    void findByNameShouldReturnUser() {
        User ivan = User.builder()
                .name("Ivan")
                .password("123")
                .id(1L)
                .build();

        Mockito.when(userRepository.findByName("Ivan")).thenReturn(Optional.ofNullable(ivan));

        User user = userService.findByName("Ivan");
        Assertions.assertEquals(ivan, user);
    }

    @Test
    void updateShouldReturnOK() {
        Optional<User> ivan = Optional.ofNullable(User.builder()
                .name("Ivan")
                .password("123")
                .id(1L)
                .build());
        Mockito.when(userRepository.findById(1L)).thenReturn(ivan);

        Assertions.assertEquals(new ResponseEntity<>("The username with id 1 has been changed", HttpStatus.OK), userService.update("Nikita", 1L));
    }

    @Test
    void updateShouldReturnBadRequest() {
        Optional<User> ivan = Optional.ofNullable(User.builder()
                .name("Ivan")
                .password("123")
                .id(1L)
                .build());
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertEquals(new ResponseEntity<>("The user with ID 1 doesn't exist ", HttpStatus.BAD_REQUEST), userService.update("Nikita", 1L));
    }







}
