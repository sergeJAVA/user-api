package com.example.user_api.service;

import com.example.user_api.mapper.UserMapper;
import com.example.user_api.model.entity.User;
import com.example.user_api.model.dto.UserDto;
import com.example.user_api.repository.UserRepository;
import com.example.user_api.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
    private final JwtService jwtService;
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
    public ResponseEntity<String> update(String username, Long id, String token) {
        Optional<User> userToUpdate = userRepository.findById(id);
        Optional<User> existUsername = userRepository.findByName(username);
        if (userToUpdate.isPresent() && existUsername.isEmpty()){
            userToUpdate.get().setName(username);

            userRepository.save(userToUpdate.get());

            String refreshedToken = jwtService.refreshJwtToken(jwtService.parseToken(token), username);

            ResponseCookie cookie = ResponseCookie.from("token", refreshedToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(604800)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
            return ResponseEntity.ok().headers(headers).body("The username with id " + id + " has been changed");
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
    public User updatePassword(String oldPassword,String newPassword, Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return user;
            }
        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            userRepository.deleteById(id);
            return new ResponseEntity<>("The user with " + id + " has been deleted", HttpStatus.OK);
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
