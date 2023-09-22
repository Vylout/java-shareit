package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.Objects;

import static ru.practicum.shareit.utils.Constants.SORT_BY_START_DESC;
import static ru.practicum.shareit.utils.ValidationErrors.*;

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
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new ElementNotFoundException(USER_NOT_FOUND);
        }
        if (!item.getAvailable()) {
            throw new ValidationException(ITEM_INACCESSIBLE);
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException(INVALID_TIME);
        }
        User user = getUser(userId);
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        return bookingRepository.save(booking);
    }

    public Booking approved(Long bookingId, boolean approved, Long userId) {
        Booking booking = getBooking(bookingId);
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new ElementNotFoundException(USER_NOT_FOUND);
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException(STATUS_CHANGED);
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    public Booking getBookingByUser(Long bookingId, Long userId) {
        Booking booking = getBooking(bookingId);
        if (!Objects.equals(booking.getBooker().getId(), userId) && !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new ElementNotFoundException(USER_NOT_FOUND);
        }
        return booking;
    }

    public List<Booking> getAllBookings(BookingState state, Long userId, int from, int size) {
        User booker =  getUser(userId);
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_START_DESC);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                return bookingRepository.findByBookerIdCurrent(userId, now, pageable).toList();
            case PAST:
                return bookingRepository.findByBookerIdAndEndIsBefore(userId, now, pageable).toList();
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartIsAfter(userId, now, pageable).toList();
            case WAITING:
                return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable).toList();
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable).toList();
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException(UNSUPPORTED_STATUS);
            case ALL:
            default:
                return bookingRepository.findAllByBooker(booker, pageable).toList();
        }
    }

    public Collection<Booking> getAllBookingsByOwner(BookingState state, Long ownerId, int from, int size) {
        User owner = getUser(ownerId);
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_START_DESC);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                return bookingRepository.findBookingsByItemOwnerCurrent(owner, now, pageable).toList();
            case PAST:
                return bookingRepository.findBookingByItemOwnerAndEndIsBefore(owner, now, pageable).toList();
            case FUTURE:
                return bookingRepository.findBookingByItemOwnerAndStartIsAfter(owner, now, pageable).toList();
            case WAITING:
                return bookingRepository.findBookingByItemOwnerAndStatus(owner, BookingStatus.WAITING, pageable).toList();
            case REJECTED:
                return bookingRepository.findBookingByItemOwnerAndStatus(owner, BookingStatus.REJECTED, pageable).toList();
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException(UNSUPPORTED_STATUS);
            case ALL:
            default:
                return bookingRepository.findBookingByItemOwner(owner, pageable).toList();
        }
    }

    public Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new ElementNotFoundException(INVALID_BOOKING));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ElementNotFoundException(INVALID_ITEM));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ElementNotFoundException(USER_NOT_FOUND));
    }
}
