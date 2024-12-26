package com.example.user_api.mapper;

import com.example.user_api.model.entity.User;
import com.example.user_api.model.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toUser(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .password(userDto.getPassword())
                .role(userDto.getRole())
                .build();
    }
}
