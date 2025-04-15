package com.example.user_api.config.security;

import com.example.user_api.service.security.JwtService;
import com.example.user_api.service.security.TokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtPerRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = TokenUtil.parseToken(request);

        if (Optional.ofNullable(token).isPresent()) {
            if (jwtService.isTokenExpired(token)) {
                if (!jwtService.isTokenWithinOneHourOfExpiration(token)) {
                    sendErrorResponse(response, "Token has expired for more than an hour. Please log in again!", HttpStatus.UNAUTHORIZED);
                    return;
                }
                token = jwtService.refreshJwtToken(jwtService.parseExpiredTokenData(token));
                setJwtCookie(response, token, 604800);
            }
            TokenAuthentication authentication = new TokenAuthentication(jwtService.parseToken(token));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private void setJwtCookie(HttpServletResponse response, String token, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true) // Рекомендуется для безопасности
                .secure(true) // Отправлять только по HTTPS
                .path("/") // Или другой путь, в зависимости от требований
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearJwtCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // Установка maxAge в 0 удаляет Cookie
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void sendErrorResponse(HttpServletResponse response, String message, HttpStatus status)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
