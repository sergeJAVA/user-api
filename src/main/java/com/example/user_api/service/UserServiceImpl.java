package com.example.user_api.service;

import com.example.user_api.mapper.UserMapper;
import com.example.user_api.model.entity.User;
import com.example.user_api.model.dto.UserDto;
import com.example.user_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByName(String username) {
        return userRepository.findByName(username).orElse(null);
    }

    @Override
    public User save(UserDto userDto) {
        return userRepository.save(userMapper.toUser(userDto));
    }

    @Override
    public ResponseEntity<String> update(String username, Long id) {
        Optional<User> updatedUser = userRepository.findById(id);
        if (updatedUser.isPresent()){
            updatedUser.get().setName(username);
            userRepository.save(updatedUser.get());
            return new ResponseEntity<>("The username with id " + id + " has been changed",HttpStatus.OK);
        }

        return new ResponseEntity<>("The user with ID " + id + " doesn't exist ", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> update(String username, String password, Long id) {
        Optional<User> updatedUser = userRepository.findById(id);
        if (updatedUser.isPresent()){
            updatedUser.get().setName(username);
            updatedUser.get().setPassword(password);
            userRepository.save(updatedUser.get());
            return new ResponseEntity<>("Username and password on id " + id + " have been changed",HttpStatus.OK);
        }
        return new ResponseEntity<>("The user with ID " + id + " doesn't exist ", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> deleteById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            userRepository.deleteById(id);
            return new ResponseEntity<>("The user with " + id + " has been deleted",HttpStatus.OK);
        }

        return new ResponseEntity<>("The user with id " + id + " doesn't exist", HttpStatus.NOT_FOUND);
    }

    @Override
    public void saveAll(List<User> users) {
        userRepository.saveAll(users);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
