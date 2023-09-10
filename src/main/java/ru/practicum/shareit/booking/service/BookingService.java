package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.ElementNotFoundException;
import ru.practicum.shareit.exeption.UnsupportedStatusException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.utils.Constants.SORT_BY_START_DESC;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public Booking addBooking(BookingDto bookingDto, Long userId) {
        Item item = getItem(bookingDto.getItemId());
        if (item.getOwner().getId() == userId) {
            throw new ElementNotFoundException("Пользователь");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Указано не верное время бронирования");
        }
        User user = getUser(userId);
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        return bookingRepository.save(booking);
    }

    public Booking approved(Long bookingId, boolean approved, Long userId) {
        Booking booking = getBooking(bookingId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new ElementNotFoundException("Пользователь");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус уже изменен");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    public Booking getBookingByUser(Long bookingId, Long userId) {
        Booking booking = getBooking(bookingId);
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new ElementNotFoundException("Пользователь");
        }
        return booking;
    }

    public List<Booking> getAllBookings(BookingState state, Long userId) {
        getUser(userId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                return bookingRepository.findByBookerIdCurrent(userId, now, SORT_BY_START_DESC);
            case PAST:
                return bookingRepository.findByBookerIdAndEndIsBefore(userId, now, SORT_BY_START_DESC);
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartIsAfter(userId, now, SORT_BY_START_DESC);
            case WAITING:
                return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, SORT_BY_START_DESC);
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, SORT_BY_START_DESC);
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            case ALL:
            default:
                return bookingRepository.findByBookerId(userId, SORT_BY_START_DESC);
        }
    }

    public Collection<Booking> getAllBookingsByOwner(BookingState state, Long ownerId) {
        User owner = getUser(ownerId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                return bookingRepository.findBookingsByItemOwnerCurrent(owner, now, SORT_BY_START_DESC);
            case PAST:
                return bookingRepository.findBookingByItemOwnerAndEndIsBefore(owner, now, SORT_BY_START_DESC);
            case FUTURE:
                return bookingRepository.findBookingByItemOwnerAndStartIsAfter(owner, now, SORT_BY_START_DESC);
            case WAITING:
                return bookingRepository.findBookingByItemOwnerAndStatus(owner, BookingStatus.WAITING, SORT_BY_START_DESC);
            case REJECTED:
                return bookingRepository.findBookingByItemOwnerAndStatus(owner, BookingStatus.REJECTED, SORT_BY_START_DESC);
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            case ALL:
            default:
                return bookingRepository.findBookingByItemOwner(owner, SORT_BY_START_DESC);
        }
    }

    public Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new ElementNotFoundException(String.format("Бронирование с ID " + bookingId)));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ElementNotFoundException(String.format("Вещь с ID " + itemId)));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ElementNotFoundException(String.format("Пользователь с ID " + userId)));
    }
}
