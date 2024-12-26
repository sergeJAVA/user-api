package com.example.user_api.service;

import com.example.user_api.model.entity.User;
import com.example.user_api.model.dto.UserDto;

import java.util.List;

public interface UserService {
    List<User> findAll();


    User findById(Long id);

    User findByName(String username);

    User save(UserDto userDto);

    User update(User user);

    void deleteById(Long id);

    void saveAll(List<User> users);
}
