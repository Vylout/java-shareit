package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Не указано название вещи")
    private String name;
    @NotBlank(message = "Не указано описание вещи")
    private String description;
    @NotNull(message = "Не указан статус доступности вещи")
    private Boolean available;
}
