package ru.practicum.shareit.exeption;

public class UniqueEmailException extends RuntimeException {

    public UniqueEmailException(String message) {
        super(message);
    }
}
