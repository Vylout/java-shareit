package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

import static ru.practicum.shareit.utils.Constants.USER_ID_HEADER;

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
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на добавление вещи");
        Item item = itemService.addItem(userId, itemDto);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable("id") Long itemId, @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Запрос на обновление данных вещи с id = {}", itemId);
        Item item = itemService.updateItem(ItemMapper.toItem(itemId, itemDto), ownerId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на получение списка вещей пользователя с id {}", userId);
        Collection<Item> items = itemService.getAllItemsByUser(userId);
        return ItemMapper.toCollection(items);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable("id") Long itemId) {
        log.info("Запрос на получение вещи с id {}", itemId);
        return ItemMapper.toItemDto(itemService.getItemById(itemId));
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItemByText(@RequestParam String text) {
        log.info("Запрос поиска вещи по тексту: {}", text);
        return ItemMapper.toCollection(itemService.findItemByText(text));
    }
}
