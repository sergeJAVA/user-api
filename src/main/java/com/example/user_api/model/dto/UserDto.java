package com.example.user_api.model.dto;

import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String name;
    private String password;
    private Set<String> roles;
}
