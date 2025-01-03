package com.example.user_api.service;

import com.example.user_api.mapper.UserMapper;
import com.example.user_api.model.entity.User;
import com.example.user_api.model.dto.UserDto;
import com.example.user_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
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
    public User update(User user) {
        User updatedUser = new User();
        if (user != null){
            updatedUser.setName(user.getName());
            updatedUser.setPassword(user.getPassword());
        }
        return userRepository.save(updatedUser);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void saveAll(List<User> users) {
        userRepository.saveAll(users);
    }
}
