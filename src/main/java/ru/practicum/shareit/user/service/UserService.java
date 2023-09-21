package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.ElementNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static ru.practicum.shareit.utils.ValidationErrors.USER_NOT_FOUND;

@Slf4j
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ElementNotFoundException(USER_NOT_FOUND));
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(User user) {
        User old = userRepository.findById(user.getId()).orElseThrow(() ->
                new ElementNotFoundException(USER_NOT_FOUND));
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
