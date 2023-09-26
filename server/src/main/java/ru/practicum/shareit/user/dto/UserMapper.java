package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(Long id, UserDto userDto) {
        return User.builder()
                .id(id)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static Collection<UserDto> toCollectionUserDto(Collection<User> users) {
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
