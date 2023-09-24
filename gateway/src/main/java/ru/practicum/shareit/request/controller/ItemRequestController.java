package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utils.ConstantsClient.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody ItemRequestDto requestDto,
                                      @RequestHeader(USER_ID_HEADER) Long requesterId) {
        log.info("Запрос на добавление запроса нужной вещи от пользователя {}", requesterId);
        return itemRequestClient.create(requestDto, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(USER_ID_HEADER) Long requesterId) {
        log.info("Запрос на получение списка запросов пользователя {}", requesterId);
        return itemRequestClient.getForOwner(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                         @PositiveOrZero int from,
                                         @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                         @Positive int size,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на получение всех запросот от других пользователей");
        return itemRequestClient.getAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable Long requestId,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос от пользователя {} на просмотр запросов от пользователя {}", userId, requestId);
        return itemRequestClient.getById(requestId, userId);
    }

}
