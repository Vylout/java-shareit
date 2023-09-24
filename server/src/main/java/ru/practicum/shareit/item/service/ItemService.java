package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.ElementNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utils.Constants.*;
import static ru.practicum.shareit.utils.ValidationErrors.INVALID_ITEM;
import static ru.practicum.shareit.utils.ValidationErrors.USER_NOT_FOUND;

@Slf4j
@Service
public class ItemService {
    private static final Comparator<Booking> BOOKING_COMPARATOR = Comparator.comparing(Booking::getStart);
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository,
                       CommentRepository commentRepository, BookingRepository bookingRepository,
                       ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new ElementNotFoundException(INVALID_ITEM));
    }

    public Item addItem(Long userId, ItemDto itemDto) {
        User user = getUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemDto.getRequestId()).orElse(null);
        }
        item.setItemRequest(request);
        return itemRepository.save(item);
    }

    public Item updateItem(Item item, Long ownerId) {
        User owner = getUser(ownerId);
        item.setOwner(owner);

        Item updateItem = getItemById(item.getId());
        if (item.getName() != null && !item.getName().isBlank()) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            updateItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
        itemRepository.save(updateItem);
        return updateItem;
    }

    public List<ResponseItemDto> getAllItemsByUser(Long userId, int from, int size) {
        User owner = getUser(userId);
        Collection<Item> items = itemRepository.findAllByOwnerOrderById(owner, PageRequest.of(from,size)).toList();
        return findInformationForResponseItemDto(items);
    }

    public ResponseItemDto getItemByUser(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        List<Comment> comments = commentRepository.findByItemId(itemId);
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (Objects.equals(item.getOwner().getId(), userId)) {
            lastBooking = bookingRepository.findBookingByItemIdAndStartBefore(item.getId(), now, SORT_BY_START_DESC).stream().findFirst().orElse(null);
            nextBooking = bookingRepository.findBookingByItemIdAndStartAfterAndStatus(item.getId(), now, BookingStatus.APPROVED, SORT_BY_START).stream().findFirst().orElse(null);
        }
        return ItemMapper.toResponseItemDto(item, lastBooking, nextBooking, comments);
    }

    @Transactional(readOnly = true)
    public List<ResponseItemDto> findItemByText(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return Collections.EMPTY_LIST;
        }
        List<Item> itemsList = itemRepository.search(text, PageRequest.of(from,size));
        return findInformationForResponseItemDto(itemsList);
    }

    public ResponseCommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        Item item = getItemById(itemId);
        User user = getUser(userId);
        Collection<Booking> bookings = bookingRepository.findBookingByItemIdAndBookerIdAndStatusAndStartBefore(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        if (bookings == null || bookings.isEmpty()) {
            throw new ValidationException(USER_NOT_FOUND);
        }
        Comment comment = CommentMapper.toComment(commentDto, item, user, LocalDateTime.now());
        comment = commentRepository.save(comment);
        return CommentMapper.toResponseCommentDto(comment);
    }

    private List<ResponseItemDto> findInformationForResponseItemDto(Collection<Item> items) {
        Map<Item, List<Booking>> bookingsByItem = getApprovedBookingsByItem(items);
        Map<Item, List<Comment>> comments = getComment(items);
        LocalDateTime now = LocalDateTime.now();
        return items.stream()
                .map(item -> getResponseItemDto(item, bookingsByItem.get(item), comments.get(item), now))
                .collect(Collectors.toList());
    }

    private ResponseItemDto getResponseItemDto(Item item, List<Booking> bookings, List<Comment> comments, LocalDateTime now) {
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (bookings != null && !bookings.isEmpty()) {
            lastBooking = bookings.stream().sorted(BOOKING_COMPARATOR)
                    .filter(booking -> booking.getStart().isBefore(now))
                    .findFirst().orElse(null);
            nextBooking = bookings.stream().sorted(BOOKING_COMPARATOR)
                    .filter(booking -> booking.getStart().isAfter(now))
                    .findFirst().orElse(null);
        }
        return ItemMapper.toResponseItemDto(item, lastBooking, nextBooking, comments);
    }

    private Map<Item, List<Booking>> getApprovedBookingsByItem(Collection<Item> items) {
        return bookingRepository.findBookingByItemInAndStatus(items, BookingStatus.APPROVED).stream()
                .collect(Collectors.groupingBy(Booking::getItem, Collectors.toList()));
    }

    private Map<Item, List<Comment>> getComment(Collection<Item> items) {
        return commentRepository.findByItemIn(items).stream().collect(Collectors.groupingBy(Comment::getItem));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ElementNotFoundException(USER_NOT_FOUND));
    }
}
