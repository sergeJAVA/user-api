package com.example.user_api.controller;

import com.example.user_api.model.entity.User;
import com.example.user_api.model.dto.UserDto;
import com.example.user_api.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public String home(){
        return "Hello";
    }

    @GetMapping("/all-users")
    public List<User> users() {
        return userService.findAll();
    }

    @GetMapping("/findId/{id}")
    public User userById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/find/{username}")
    public User userByName(@PathVariable String username) {
        return userService.findByName(username);
    }

    @PostMapping("/create")
    public User createUser(@RequestBody UserDto user) {
        return userService.save(user);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable Long id) {
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostConstruct
    public void loadUser(){
        List<User> users = new ArrayList<>();

        User serega = new User();
        User nikodim = new User();

        serega.setName("Serega");
        serega.setPassword("12345");

        nikodim.setName("Nikodim");
        nikodim.setPassword("password");
        nikodim.setRole("user");

        users.add(nikodim);
        users.add(serega);

        userService.saveAll(users);
    }
}
