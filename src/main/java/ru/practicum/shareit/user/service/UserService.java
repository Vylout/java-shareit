package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ElementNotFoundException;
import ru.practicum.shareit.exeption.UniqueEmailException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.repository.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Map<Long, User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        if (!userStorage.getAllUsers().containsKey(id)) {
            log.error("Не найден пользователь с ID {}", id);
            throw new ElementNotFoundException(String.format("Пользователь с ID " + id));
        }
        return userStorage.getUserById(id);
    }

    public User addUser(User user) {
        checkValidUser(user);
        checkUniqueEmail(user.getEmail(), user.getId());
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        checkId(user.getId());
        User old = userStorage.getUserById(user.getId());
        if (user.getName() != null) {
            old.setName(user.getName());
        }
        if (user.getEmail() != null) {
            checkUniqueEmail(user.getEmail(), user.getId());
            old.setEmail(user.getEmail());
        }
        userStorage.updateUser(old);
        return old;
    }

    public Long removeUser(Long id) {
        return userStorage.removeUser(id);
    }

    private void checkValidUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("Имя пользователя указано пустым.");
            throw new ValidationException("Имя пользователя указано пустым.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.error("Указан не верный Email.");
            throw new ValidationException("Указан не верный Email");
        }
    }

    private void checkUniqueEmail(String email, Long id) {
        for (User user : getAllUsers().values()) {
            if (user.getEmail().equals(email)) {
                if (!user.getId().equals(id)) {
                    log.error("Email {} уже зарегистрирован.", email);
                    throw new UniqueEmailException("Данный Email уже зарегистрирован.");
                }
            }
        }
    }

    private void checkId(Long id) {
        if (!id.equals(getUserById(id).getId())) {
            log.error("Пользователь не найден.");
            throw new ElementNotFoundException("Данный пользователь");
        }
    }
}
