package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.Collection;

import static ru.practicum.shareit.utils.Constants.*;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto,
                           @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на добавление вещи");
        Item item = itemService.addItem(userId, itemDto);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable("id") Long itemId,
                              @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Запрос на обновление данных вещи с id = {}", itemId);
        Item item = itemService.updateItem(ItemMapper.toItem(itemId, itemDto), ownerId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping("/{id}")
    public ResponseItemDto getItemById(@PathVariable("id") Long itemId,
                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на получение вещи с id {}", itemId);
        return itemService.getItemByUser(itemId, userId);
    }

    @GetMapping
    public Collection<ResponseItemDto> getAllItemsByUser(@RequestHeader(USER_ID_HEADER) Long userId,
                                                         @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                            int from,
                                                         @RequestParam(defaultValue =  DEFAULT_SIZE_VALUE)
                                                            int size) {
        log.info("Запрос на получение списка вещей пользователя с id {}", userId);
        return itemService.getAllItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ResponseItemDto> findItemByText(@RequestParam String text,
                                              @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                int from,
                                              @RequestParam(defaultValue =  DEFAULT_SIZE_VALUE)
                                                int size) {
        log.info("Запрос поиска вещи по тексту: {}", text);
        return itemService.findItemByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseCommentDto addComment(@Valid @RequestBody CommentDto commentDto,
                                         @PathVariable Long itemId,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на добавление сомментария от ползователя {} к вещи {}", userId, itemId);
        return itemService.addComment(commentDto, itemId, userId);
    }
}
