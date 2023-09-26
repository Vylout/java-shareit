package ru.practicum.shareit.exeption;

public class ArgumentException extends RuntimeException {
    public ArgumentException(String message) {
        super(message);
    }
}
