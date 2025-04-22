package com.example.user_api.service;

import com.example.user_api.model.entity.User;
import com.example.user_api.model.dto.UserDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(Long id);

    User findByName(String username);

    User save(UserDto userDto);

    ResponseEntity<String> update(String username, Long id, String token);

    ResponseEntity<String> update(String username, String password, Long id);

    User updatePassword(String oldPassword,String newPassword, Long id);

    ResponseEntity<String> deleteById(Long id);

    void saveAll(List<User> users);

    void deleteAll();
}
