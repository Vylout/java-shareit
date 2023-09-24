package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestUtils.*;
import static ru.practicum.shareit.utils.Constants.SORT_BY_START;
import static ru.practicum.shareit.utils.Constants.SORT_BY_START_DESC;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemService itemService;

    @Test
    void addItemTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                request.getId());
        Item createdItem = itemService.addItem(1L, itemDto);

        assertNotNull(createdItem);
        assertEquals(createdItem, item);
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item updateItem = itemService.updateItem(item, 1L);

        assertNotNull(updateItem);
        assertEquals(updateItem, item);
        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void getItemForUser() {
        User commentAuthor = new User(2L, "user_name2", "user2@mail.com");
        List<Comment> comments = List.of(new Comment(1L, "text", item, commentAuthor, LocalDateTime.now()));
        Booking lastBooking = Booking.builder().id(1L).item(item).booker(commentAuthor).build();
        Booking nextBooking = Booking.builder().id(2L).item(item).booker(commentAuthor).build();
        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(1L))
                .thenReturn(comments);
        when(bookingRepository.findBookingByItemIdAndStartBefore(eq(1L), any(LocalDateTime.class), eq(SORT_BY_START_DESC)))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findBookingByItemIdAndStartAfterAndStatus(eq(1L), any(LocalDateTime.class), eq(BookingStatus.APPROVED), eq(SORT_BY_START)))
                .thenReturn(List.of(nextBooking));

        ResponseItemDto gottenItemDto = itemService.getItemByUser(1L, 1L);

        assertNotNull(gottenItemDto);
        assertEquals(gottenItemDto, ItemMapper.toResponseItemDto(item, lastBooking, nextBooking, comments));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(1)).findBookingByItemIdAndStartBefore(eq(1L), any(LocalDateTime.class), eq(SORT_BY_START_DESC));
        verify(bookingRepository, times(1)).findBookingByItemIdAndStartAfterAndStatus(eq(1L), any(LocalDateTime.class), eq(BookingStatus.APPROVED), eq(SORT_BY_START));
    }

    @Test
    void getAllTest() {
        User user1 = new User(1L, "user1", "user@mail.com");
        User user2 = new User(2L, "user2", "user2@mail.com");
        Item item1 = Item.builder().id(1L).name("item1").description("item_description1").owner(user2).build();
        Item item2 = Item.builder().id(1L).name("item2").description("item_description2").owner(user2).build();
        Booking lastBooking1 = Booking.builder().id(1L).item(item1).booker(user1).start(LocalDateTime.now().minusDays(1)).build();
        Booking nextBooking1 = Booking.builder().id(2L).item(item1).booker(user1).start(LocalDateTime.now().plusDays(1)).build();
        Booking lastBooking2 = Booking.builder().id(3L).item(item2).booker(user1).start(LocalDateTime.now().minusDays(1)).build();
        Booking nextBooking2 = Booking.builder().id(4L).item(item2).booker(user1).start(LocalDateTime.now().plusDays(1)).build();

        List<Comment> comments = List.of(new Comment(1L, "text", item1, user1, LocalDateTime.now()));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerOrderById(user1, PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(List.of(item1, item2)));
        when(bookingRepository.findBookingByItemInAndStatus(any(), eq(BookingStatus.APPROVED)))
                .thenReturn(List.of(lastBooking1, nextBooking1, lastBooking2, nextBooking2));
        when(commentRepository.findByItemIn(any()))
                .thenReturn(comments);

        Collection<ResponseItemDto> result = itemService.getAllItemsByUser(1L, 0, 20);
        ResponseItemDto itemDto1 = ResponseItemDto.builder().id(item1.getId()).name(item1.getName()).description(item1.getDescription())
                .lastBooking(BookingMapper.toBookingReferencedDto(lastBooking1))
                .nextBooking(BookingMapper.toBookingReferencedDto(nextBooking1))
                .comments(CommentMapper.toCollectionResponseCommentDto(comments))
                .build();
        ResponseItemDto itemDto2 = ResponseItemDto.builder().id(item2.getId()).name(item2.getName()).description(item2.getDescription())
                .lastBooking(BookingMapper.toBookingReferencedDto(lastBooking2))
                .nextBooking(BookingMapper.toBookingReferencedDto(nextBooking2))
                .build();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(itemDto1));
        assertTrue(result.contains(itemDto2));
        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findAllByOwnerOrderById(user1, PageRequest.of(0, 20));
        verify(bookingRepository, times(1)).findBookingByItemInAndStatus(any(), eq(BookingStatus.APPROVED));
        verify(commentRepository, times(1)).findByItemIn(any());
    }

    @Test
    void findItemsByTextTest() {
        Collection<ResponseItemDto> emptyResult = itemService.findItemByText("", 0, 20);
        assertTrue(emptyResult.isEmpty());
        String text = "текст";
        when(itemRepository.search(text, PageRequest.of(0, 20)))
                .thenReturn(List.of(item));
        List<ResponseItemDto> result = itemService.findItemByText(text, 0, 20);
        verify(itemRepository, times(1)).search(text, PageRequest.of(0, 20));
        assertNotNull(result);
        assertEquals(1, result.size());
        ResponseItemDto itemDto = ResponseItemDto.builder().id(item.getId()).name(item.getName()).description(item.getDescription())
                .available(item.getAvailable()).requestId(item.getItemRequest().getId()).build();
        assertTrue(result.contains(itemDto));
    }

    @Test
    void createCommentTest() {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = Comment.builder().id(1L).text("text").author(booker).item(item).created(now).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItemIdAndBookerIdAndStatusAndStartBefore(eq(1L), eq(1L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto commentDto = new CommentDto("text");
        ResponseCommentDto result = itemService.addComment(commentDto, 1L, 1L);

        assertNotNull(result);
        ResponseCommentDto dto = ResponseCommentDto.builder().id(1L).text("text").authorName(booker.getName()).created(now).build();
        assertEquals(result, dto);
    }
}
