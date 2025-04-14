package com.example.user_api.service.security;

import com.example.user_api.model.security.TokenData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    public Long getUserIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("userId", Long.class));
    }

    public String getUserNameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public List<String> getRolesFromToken(String token) {
        return getClaimFromToken(token, (Function<Claims, List<String>>) claims -> claims.get("roles", List.class));
    }


    private <T> T getClaimFromToken(String token, Function<Claims, T> claimResolver) {
        return claimResolver.apply(getAllClaimsFromToken(token));
    }

    public TokenData parseToken(String token) {
        return TokenData.builder()
                .token(token)
                .username(getUserNameFromToken(token))
                .authorities(getRolesFromToken(token).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
                .id(getUserIdFromToken(token))
                .build();
    }

    @SneakyThrows
    private Claims getAllClaimsFromToken(String token){
        try{
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error! Wrong argument passed!");
        }

    }

    public TokenData parseExpiredTokenData(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .clockSkewSeconds(60)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return TokenData.builder()
                    .token(token)
                    .username(claims.getSubject())
                    .authorities(getAuthoritiesFromClaims(claims))
                    .id(claims.get("userId", Long.class))
                    .build();
        } catch (ExpiredJwtException e) {
            Claims claims = e.getClaims();
            return TokenData.builder()
                    .token(token)
                    .username(claims.getSubject())
                    .authorities(getAuthoritiesFromClaims(claims))
                    .id(claims.get("userId", Long.class))
                    .build();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error parsing expired token: {}", e.getMessage());
            throw new IllegalArgumentException("Unable to parse expired token");
        }
    }

    private List<SimpleGrantedAuthority> getAuthoritiesFromClaims(Claims claims) {
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof Collection<?> roles) {
            return roles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public boolean isTokenExpired(String token) {
        try {
            return getAllClaimsFromToken(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error while parsing token for expiration check: {}", e.getMessage());
            return true;
        }
    }

    public String refreshJwtToken(TokenData tokenData) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("username", tokenData.getUsername());
        claims.put("roles", tokenData.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet()));
        claims.put("userId", tokenData.getId());

        log.info("Jwt has been refreshed!");
        return Jwts.builder()
                .claims(claims)
                .subject(tokenData.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1800000))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}
