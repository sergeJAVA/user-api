package com.example.user_api.service;

import com.example.user_api.model.entity.User;
import com.example.user_api.model.dto.UserDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();


    User findById(Long id);

    User findByName(String username);

    User save(UserDto userDto);

    ResponseEntity<String> update(String username, Long id);

    ResponseEntity<String> update(String username, String password, Long id);

    void deleteById(Long id);

    void saveAll(List<User> users);
}
