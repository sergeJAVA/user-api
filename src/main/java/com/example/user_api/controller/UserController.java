package com.example.user_api.controller;

import com.example.user_api.constants.RoleType;
import com.example.user_api.model.entity.User;
import com.example.user_api.model.dto.UserDto;
import com.example.user_api.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        return userService.deleteById(id);
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAll() {
        userService.deleteAll();
        return new ResponseEntity<>("All users have been deleted from the database", HttpStatus.OK);
    }

    @PostConstruct
    public void loadUser(){
        List<User> users = new ArrayList<>();

        User serega = new User();
        User nikodim = new User();

        serega.setName("Serega");
        serega.setPassword(passwordEncoder.encode("12345"));
        serega.setRoles(Set.of(RoleType.USER.value()));

        nikodim.setName("Kolya");
        nikodim.setPassword(passwordEncoder.encode("password"));
        nikodim.setRoles(Set.of(RoleType.ADMIN.value(), RoleType.USER.value()));

        users.add(nikodim);
        users.add(serega);

        userService.saveAll(users);
    }
}
