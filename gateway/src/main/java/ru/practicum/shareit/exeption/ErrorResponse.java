package ru.practicum.shareit.exeption;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private int code;
    private String status;
    private String error;
}
