package com.example.user_api.model.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenData {
    private Long id;
    private String userName;
    private String token;
    private List<? extends GrantedAuthority> authorities;
}
