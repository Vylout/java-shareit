package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public Map<Long, User> getAllUsers() {
        return users;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        log.warn("Новый пользователь зарегистрирован.");
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.warn("Данные пользователя обновлены.");
        return user;
    }

    @Override
    public Long removeUser(Long id) {
        users.remove(id);
        log.warn("Пользователь с ID {} удален.", id);
        return id;
    }

    private Long generateId() {
        return id++;
    }
}
