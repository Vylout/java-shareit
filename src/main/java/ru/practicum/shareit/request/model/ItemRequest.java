package ru.practicum.shareit.request.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ItemRequest {
    private Long id;
    private String description;
    private Long requestor;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate created;
}
