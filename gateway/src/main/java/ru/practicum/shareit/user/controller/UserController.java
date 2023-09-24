package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping("{id}")
    public ResponseEntity<Object> get(@PathVariable Long id) {
        log.info("Запрос на получение пользователя с ID {}", id);
        return userClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Запрос на получение списка пользователей");
        return userClient.getAll();
    }

    @PostMapping
    public ResponseEntity<Object> add(@Validated() @RequestBody UserDto userDto) {
        log.info("Запрос на добавление пользователя");
        return userClient.create(userDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@RequestBody UserDto userDto,
                                         @PathVariable("id") Long id) {
        log.info("Запрос на обновление данных пользователя с ID {}", id);
        return userClient.update(userDto, id);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Запрос на удаление пользователя с ID {}", id);
        return userClient.deleteById(id);
    }
}
