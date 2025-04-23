package com.example.user_api.controller;

import com.example.user_api.model.dto.UserDto;
import com.example.user_api.model.entity.User;
import com.example.user_api.service.UserService;
import com.example.user_api.service.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @LocalServerPort
    private int port;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/user";
        userService.deleteAll();
        User serega = new User(1L, "Serega", passwordEncoder.encode("12345"), Set.of("user"));
        User kolya = new User(2L, "Kolya", passwordEncoder.encode("password"), Set.of("user"));
        userService.saveAll(List.of(serega, kolya));
    }

    @Test
    void testGetAllUsers() {
        HttpEntity<String> request = new HttpEntity<>(createHeaders());
        ResponseEntity<User[]> response = restTemplate.exchange(
                baseUrl + "/all-users",
                HttpMethod.GET,
                request,
                User[].class
        );
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User[] users = response.getBody();
        assertNotNull(users, "Users array should not be null");
        assertEquals(2, users.length);
        assertTrue(List.of(users).stream().anyMatch(u -> u.getName().equals("Serega")));
    }

    @Test
    void testGetUserById() {
        User user = userService.findByName("Serega");
        String token = jwtService.generateJwtTokenForTests(user.getId(), user.getName());
        HttpHeaders headers = createHeaders();
        headers.add(HttpHeaders.COOKIE, "token=" + token);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<User> response = restTemplate.exchange(
                baseUrl + "/find/id",
                HttpMethod.GET,
                request,
                User.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        User result = response.getBody();
        assertNotNull(result);
        assertEquals(response.getBody().getName(), "Serega");
    }

    @Test
    void testPostCreateUser() {
        UserDto userDto = new UserDto("NewUser", "password", Set.of("user"));
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDto> request = new HttpEntity<>(userDto, headers);

        ResponseEntity<User> response = restTemplate.exchange(
                baseUrl + "/create",
                HttpMethod.POST,
                request,
                User.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User createdUser = response.getBody();
        assertNotNull(createdUser);
        assertEquals("NewUser", createdUser.getName());
    }

    @Test
    void testPostUpdateUsername() {
        User user = userService.findByName("Serega");
        String token = jwtService.generateJwtTokenForTests(user.getId(), user.getName());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "NewSerega");

        HttpHeaders headers = createHeaders();
        headers.add(HttpHeaders.COOKIE, "token=" + token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/update-username",
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        User updatedUser = userService.findById(user.getId());
        assertEquals("NewSerega", updatedUser.getName());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
        assertEquals("The username with id " + user.getId() + " has been changed", response.getBody());
    }

    @Test
    void testPostUpdateUserFull() {
        User user = userService.findByName("Kolya");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", user.getId().toString());
        params.add("username", "NewKolya");
        params.add("password", "newpass");

        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/update",
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        User updatedUser = userService.findById(user.getId());
        assertEquals("NewKolya", updatedUser.getName());
        assertTrue(passwordEncoder.matches("newpass", updatedUser.getPassword()));
    }

    @Test
    void testDeleteUser() {
        User user = userService.findByName("Serega");
        HttpEntity<String> request = new HttpEntity<>(createHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/delete/" + user.getId(),
                HttpMethod.DELETE,
                request,
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The user with " + user.getId() + " has been deleted", response.getBody());
    }

    @Test
    void testDelete_WhenUserNotExist() {
        int idNonExistentUser = 5;
        HttpEntity<String> request = new HttpEntity<>(createHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/delete/" + idNonExistentUser,
                HttpMethod.DELETE,
                request,
                String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("The user with id " + idNonExistentUser + " doesn't exist", response.getBody());
    }

    private HttpHeaders createHeaders() {
        return new HttpHeaders();
    }
}