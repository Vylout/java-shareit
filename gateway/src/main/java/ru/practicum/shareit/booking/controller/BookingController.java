package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utils.ConstantsClient.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    ResponseEntity<Object> add(@Valid @RequestBody BookingDto postBookingDto,
                               @RequestHeader(USER_ID_HEADER) Long bookerId) {
        log.info("Запрос на добавление бронирования вещи с ID {} от пользователя с ID {}",postBookingDto.getItemId(), bookerId);
        return bookingClient.createBooking(postBookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    ResponseEntity<Object> approve(@PathVariable int bookingId, @RequestParam boolean approved,
                                   @RequestHeader(USER_ID_HEADER) int userId) {
        log.info("Запрос на одобрение бронирования");
        return bookingClient.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@PathVariable int bookingId,
                                          @RequestHeader(USER_ID_HEADER) int userId) {
        log.info("Запрос о получении данных о бронирование");
        return bookingClient.getById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookings(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                 @RequestHeader(USER_ID_HEADER) int userId,
                                                 @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                 @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                 @Positive int size) {
        log.info("Запрос на получение всех бронирований пользователя {} со статусом {} со значениями from {}, size {}", userId, state, from, size);
        return bookingClient.getAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingForOwner(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                        @RequestHeader(USER_ID_HEADER) int ownerId,
                                                        @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                        @PositiveOrZero int from,
                                                        @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                        @Positive int size) {
        log.info("Запрос от пользователя {} на получения списка забронируемых у него вещей со статусом {} со значениями from {}, size {}", ownerId, state, from, size);
        return bookingClient.getAllBookingForOwner(ownerId, state, from, size);
    }

}
