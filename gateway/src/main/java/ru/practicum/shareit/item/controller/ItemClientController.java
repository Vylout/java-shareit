package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentClientDto;
import ru.practicum.shareit.item.dto.ItemClientDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utils.ConstantsClient.*;

@Slf4j
@Validated
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemClientController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody ItemClientDto postItemDto,
                                      @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос для добовление вещи");
        return itemClient.create(postItemDto, userId);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody ItemClientDto postItemDto,
                                         @PathVariable("id") Long itemId,
                                         @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Запрос на обновление данных вещи с id = {}", itemId);
        return itemClient.update(postItemDto, itemId, ownerId);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> get(@PathVariable("id") Long itemId,
                                      @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на получение вещи с id {}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                         @PositiveOrZero int from,
                                         @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                         @Positive int size) {
        log.info("Запрос на получение списка вещей пользователя с id {}", userId);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsByText(@RequestParam String text,
                                                  @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                  @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                  @Positive int size) {
        log.info("Запрос поиска вещи по тексту: {}", text);
        return itemClient.findItemsByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentClientDto commentDto,
                                             @PathVariable Long itemId,
                                             @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на добавление сомментария от ползователя {} к вещи {}", userId, itemId);
        return itemClient.createComment(commentDto, itemId, userId);
    }
}
