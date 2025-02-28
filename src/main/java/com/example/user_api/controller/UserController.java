package com.example.user_api.controller;

import com.example.user_api.model.entity.User;
import com.example.user_api.model.dto.UserDto;
import com.example.user_api.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final BCryptPasswordEncoder passwordEncoder;

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

    @PostMapping("/update-username")
    public ResponseEntity<String> updateUser(@RequestParam Long id, @RequestParam String username) {
        return userService.update(username, id);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestParam Long id, @RequestParam String username, @RequestParam String password) {

        return userService.update(username, passwordEncoder.encode(password), id);
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
        serega.setPassword(passwordEncoder.encode("12345"));
        serega.setRole("user");

        nikodim.setName("Kolya");
        nikodim.setPassword(passwordEncoder.encode("password"));
        nikodim.setRole("user");

        users.add(nikodim);
        users.add(serega);

        userService.saveAll(users);
    }
}
