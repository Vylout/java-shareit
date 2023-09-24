package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static ru.practicum.shareit.utils.ConstantsClient.DATE_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingClientDto {
    @NotNull
    private int itemId;
    @NotNull
    @FutureOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDateTime start;
    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDateTime end;
}
