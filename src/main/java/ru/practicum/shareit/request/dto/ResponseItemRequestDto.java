package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ResponseItemRequestDto {
    private Long id;
    private String description;
    private List<ItemRequestDto> items = new ArrayList<>();
    private LocalDateTime created;
}
