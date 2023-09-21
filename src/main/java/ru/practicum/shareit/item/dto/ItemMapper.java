package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ResponseItemDto toResponseItemDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        return ResponseItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(BookingMapper.toBookingReferencrdDto(lastBooking))
                .nextBooking(BookingMapper.toBookingReferencrdDto(nextBooking))
                .comments(CommentMapper.toCollectionResponseCommentDto(comments))
                .requestId(item.getItemRequest() == null ? null : item.getItemRequest().getId())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getItemRequest() == null ? null : item.getItemRequest().getId());
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static Item toItem(Long itemId, ItemDto itemDto) {
        return Item.builder()
                .id(itemId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static Collection<ItemDto> toCollection(Collection<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static ItemRequestDto toItemForRequestDto(Item item) {
        return ItemRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest().getId())
                .build();
    }

    public static List<ItemRequestDto> toItemForRequestDto(List<Item> items) {
        if (items == null) {
            return Collections.emptyList();
        }
        return items.stream().map(ItemMapper::toItemForRequestDto).collect(Collectors.toList());
    }
}
