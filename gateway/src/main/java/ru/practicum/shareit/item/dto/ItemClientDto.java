package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemClientDto {
    private int id;
    @NotBlank(message = "Параметар имя не может быть пустым")
    private String name;
    @NotBlank(message = "Параметор описание не может быть пустым")
    private String description;
    @NotNull(message = "Не указан статус")
    private Boolean available;
    private Integer requestId;
}
