package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(PostItemRequestDto postItemRequestDto) {
        return ItemRequest.builder()
                .description(postItemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public static ResponseItemRequestDto toResponseItemRequestDto(ItemRequest itemRequest) {
        return ResponseItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ResponseItemRequestDto toResponseItemRequestDto(ItemRequest request, List<Item> items) {
        return ResponseItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(ItemMapper.toItemForRequestDto(items))
                .build();
    }
}
