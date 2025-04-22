package com.example.user_api.mapper;

import com.example.user_api.model.dto.UserDto;
import com.example.user_api.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {
    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .name(user.getName())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }
}
