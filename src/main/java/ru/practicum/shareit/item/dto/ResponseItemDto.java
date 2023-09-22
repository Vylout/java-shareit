package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingReferencrdDto;

import java.util.List;

@Data
@Builder
public class ResponseItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingReferencrdDto lastBooking;
    private BookingReferencrdDto nextBooking;
    private List<ResponseCommentDto> comments;
    private Long requestId;
}
