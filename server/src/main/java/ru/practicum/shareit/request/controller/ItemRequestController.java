package ru.practicum.shareit.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import java.util.Collection;

import static ru.practicum.shareit.utils.Constants.*;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ResponseItemRequestDto addItemRequest(@RequestBody PostItemRequestDto postItemRequestDto,
                                                 @RequestHeader(USER_ID_HEADER) Long requesterId) {
        log.info("Запрос на добавление запроса нужной вещи от пользователя {}", requesterId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(postItemRequestDto);
        return ItemRequestMapper.toResponseItemRequestDto(itemRequestService.addItemRequest(itemRequest,requesterId));
    }

    @GetMapping
    public Collection<ResponseItemRequestDto> getAllByOwner(@RequestHeader(USER_ID_HEADER) Long requesterId) {
        log.info("Запрос на получение списка запросов пользователя {}", requesterId);
        return itemRequestService.getAllItemRequestsByOwner(requesterId);
    }

    @GetMapping("/all")
    public Collection<ResponseItemRequestDto> getAllItemRequests(@RequestHeader(USER_ID_HEADER) Long requesterId,
                                                                 @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                                    int from,
                                                                 @RequestParam(defaultValue =  DEFAULT_SIZE_VALUE)
                                                                    int size) {
        log.info("Запрос на получение всех запросот от других пользователей");
        return itemRequestService.getAll(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseItemRequestDto getById(@PathVariable Long requestId,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос от пользователя {} на просмотр запросов от пользователя {}", userId, requestId);
        return itemRequestService.getById(requestId, userId);
    }
}
