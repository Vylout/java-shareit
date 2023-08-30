package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ElementNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ElementNotFoundException(String.format("Пользователь с ID " + id)));
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        User old = userRepository.findById(user.getId()).orElseThrow();
        if (user.getName() != null) {
            old.setName(user.getName());
        }
        if (user.getEmail() != null) {
            old.setEmail(user.getEmail());
        }
        userRepository.save(old);
        return old;
    }

    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }
}
