package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotNull(message = "Поле Имя не может быть пустым")
    private String name;
    @Email(message = "Указана не верная форма Email")
    @NotNull(message = "Не указан Email")
    private String email;
}
