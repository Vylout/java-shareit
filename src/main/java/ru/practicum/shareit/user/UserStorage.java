package ru.practicum.shareit.user;

import java.util.Map;

public interface UserStorage {
    User addUser(User user);

    Map<Long, User> getAllUsers();

    User getUserById(Long id);

    User updateUser(User user);

    Long removeUser(Long id);
}
