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
    @NotNull(message = "Указан пустой name")
    private String name;
    @Email(message = "Указан неверный Email")
    @NotNull(message = "Указан пустой Email")
    private String email;
}
