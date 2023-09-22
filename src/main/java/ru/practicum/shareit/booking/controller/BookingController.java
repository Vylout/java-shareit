package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.utils.Constants.*;

@Validated
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    ResponseBookingDto addBooking(@Valid @RequestBody BookingDto bookingDto,
                                  @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на бронирования вещи {} от пользователя {}", bookingDto.getItemId(), userId);
        Booking booking = bookingService.addBooking(bookingDto, userId);
        return BookingMapper.toResponseBooking(booking);
    }

    @PatchMapping("/{bookingId}")
    ResponseBookingDto approved(@PathVariable Long bookingId,
                                @RequestParam boolean approved,
                                @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на одобрение бронирования");
        Booking booking = bookingService.approved(bookingId, approved, userId);
        return BookingMapper.toResponseBooking(booking);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getById(@PathVariable Long bookingId,
                                      @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос о получении данных о бронирование");
        Booking booking = bookingService.getBookingByUser(bookingId, userId);
        return BookingMapper.toResponseBooking(booking);
    }

    @GetMapping
    public Collection<ResponseBookingDto> getAllBookings(@RequestParam(value = "state", defaultValue = "ALL")
                                                         BookingState state,
                                                         @RequestParam(value = "from", defaultValue = DEFAULT_FROM_VALUE) @Min(value = 0) int from,
                                                         @RequestParam(value = "size", defaultValue =  DEFAULT_SIZE_VALUE) int size,
                                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на получение всех бронирований пользователя {} со статусом {} со значениями from {}, size {}", userId, state, from, size);
        List<Booking> bookings = bookingService.getAllBookings(state, userId, from, size);
        return BookingMapper.toBookingReferencedDto(bookings);
    }

    @GetMapping("/owner")
    public Collection<ResponseBookingDto> getAllBookingsByOwner(@RequestParam(value = "state", defaultValue = "ALL")
                                                                BookingState state,
                                                                @RequestParam(value = "from", defaultValue = DEFAULT_FROM_VALUE) @Min(value = 0) int from,
                                                                @RequestParam(value = "size", defaultValue =  DEFAULT_SIZE_VALUE) int size,
                                                                @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Запрос от пользователя {} на получения списка забронируемых у него вещей со статусом {} со значениями from {}, size {}", ownerId, state, from, size);
        Collection<Booking> bookings = bookingService.getAllBookingsByOwner(state, ownerId, from, size);
        return BookingMapper.toBookingReferencedDto(bookings);
    }
}
