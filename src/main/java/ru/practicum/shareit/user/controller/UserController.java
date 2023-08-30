package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("Запрос на получения списка всех пользователей");
        Collection<User> users = userService.getAllUsers();
        return UserMapper.toCollectionUserDto(users);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("Запрос пользователя с id {}", id);
        return UserMapper.toDto(userService.getUserById(id));
    }

    @PostMapping
    public UserDto addUser(@Valid() @RequestBody UserDto userDto) {
        log.info("Запрос на создание нового пользователя");
        User user = userService.addUser(UserMapper.toEntity(null, userDto));
        return UserMapper.toDto(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        log.info("Запрос на обновление данных пользователя с id {}", id);
        User user = UserMapper.toEntity(id, userDto);
        return UserMapper.toDto(userService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable Long id) {
        log.info("Запрос на удаление пользователя с id {}", id);
        userService.removeUser(id);
    }
}
