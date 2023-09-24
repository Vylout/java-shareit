package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestClientDto {
    @NotNull(message = "Описание не может быть пустым")
    private String description;
}
