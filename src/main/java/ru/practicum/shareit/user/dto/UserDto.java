package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotNull(message = "Указан пустой name")
    private String name;
    @Email(message = "Указан неверный Email")
    @NotNull(message = "Указан пустой Email")
    private String email;
}
