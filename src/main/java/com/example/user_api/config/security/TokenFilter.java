package com.example.user_api.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class TokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // Проверяем заголовок Authorization
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer ")) {

            // Если Authorization отсутствует или пустой, проверяем Cookie
            Optional<String> tokenFromCookie = extractTokenFromCookie(request);

            if (tokenFromCookie.isPresent()) {
                // Если токен найден в Cookie, добавляем его в заголовок Authorization
                authHeader = "Bearer " + tokenFromCookie.get();
                // Нам нужно обернуть запрос, чтобы добавить новый заголовок
                String finalAuthHeader = authHeader;
                HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request) {
                    @Override
                    public String getHeader(String name) {
                        if ("Authorization".equalsIgnoreCase(name)) {
                            return finalAuthHeader;
                        }
                        return super.getHeader(name);
                    }
                };
                filterChain.doFilter(requestWrapper, response);
                return;
            }
        }

        // Если токен уже есть в Authorization или не найден в Cookie, продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }


    private Optional<String> extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> "token".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .filter(token -> !token.isEmpty())
                    .findFirst();
        }
        return Optional.empty();
    }
}
