package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.ElementNotFoundException;
import ru.practicum.shareit.exeption.UnsupportedStatusException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestUtils.*;
import static ru.practicum.shareit.utils.Constants.*;
import static ru.practicum.shareit.utils.ValidationErrors.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingService bookingService;

    @Test
    void addBookingTest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        BookingDto bookingDto = BookingDto.builder().itemId(item.getId()).build();
        ElementNotFoundException notFoundException = assertThrows(ElementNotFoundException.class, () -> bookingService.addBooking(bookingDto, owner.getId()));
        assertEquals(USER_NOT_FOUND, notFoundException.getMessage());

        // not available
        item.setAvailable(false);
        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingDto, booker.getId()));
        assertEquals(ITEM_INACCESSIBLE, exception.getMessage());

        // invalid start
        item.setAvailable(true);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        exception = assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingDto, booker.getId()));
        assertEquals(INVALID_TIME, exception.getMessage());

        // normal
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Booking booking = Booking.builder().id(1L).booker(booker).start(bookingDto.getStart()).end(bookingDto.getEnd()).item(item).build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        Booking result = bookingService.addBooking(bookingDto, booker.getId());
        assertNotNull(result);
        assertEquals(result, booking);
        verify(userRepository, times(1)).findById(2L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveTest() {
        Booking booking = Booking.builder().id(1L).item(item).booker(booker).build();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // invalid user Id
        ElementNotFoundException notFoundException = assertThrows(ElementNotFoundException.class, () -> bookingService.approved(1L, true, booker.getId()));
        assertEquals(USER_NOT_FOUND, notFoundException.getMessage());

        // invalid status
        booking.setStatus(BookingStatus.APPROVED);
        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.approved(1L, true, owner.getId()));
        assertEquals(STATUS_CHANGED, exception.getMessage());

        // normal
        booking.setStatus(BookingStatus.WAITING);
        Booking approvedBooking = Booking.builder().id(booking.getId()).booker(booking.getBooker())
                .item(booking.getItem()).status(BookingStatus.APPROVED).build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(approvedBooking);
        Booking result = bookingService.approved(1L, true, owner.getId());

        assertNotNull(result);
        assertEquals(result, booking);
        verify(bookingRepository, times(3)).findById(1L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void getBookingByUser() {
        Booking booking = Booking.builder().id(1L).item(item).booker(booker).build();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // invalid userId
        ElementNotFoundException exception = assertThrows(ElementNotFoundException.class, () -> bookingService.getBookingByUser(1L, 3L));
        assertEquals(USER_NOT_FOUND, exception.getMessage());

        // normal
        Booking result = bookingService.getBookingByUser(1L, 1L);
        assertNotNull(result);
        assertEquals(result, booking);
        verify(bookingRepository, times(2)).findById(1L);
    }

    @Test
    void findAllBookingsTest() {
        Booking booking = Booking.builder().id(1L).item(item).booker(booker).build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Pageable page = PageRequest.of(DEFAULT_FROM_INT, DEFAULT_SIZE_INT, SORT_BY_START_DESC);
        Page<Booking> pageResult = new PageImpl<>(List.of(booking));

        // CURRENT
        when(bookingRepository.findByBookerIdCurrent(eq(booker.getId()), any(LocalDateTime.class), eq(page))).thenReturn(pageResult);
        List<Booking> result = bookingService.getAllBookings(BookingState.CURRENT, booker.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findByBookerIdCurrent(eq(booker.getId()), any(LocalDateTime.class), eq(page));
        // PAST
        when(bookingRepository.findByBookerIdAndEndIsBefore(eq(booker.getId()), any(LocalDateTime.class), eq(page))).thenReturn(pageResult);
        result = bookingService.getAllBookings(BookingState.PAST, booker.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findByBookerIdAndEndIsBefore(eq(booker.getId()), any(LocalDateTime.class), eq(page));
        // FUTURE
        when(bookingRepository.findByBookerIdAndStartIsAfter(eq(booker.getId()), any(LocalDateTime.class), eq(page))).thenReturn(pageResult);
        result = bookingService.getAllBookings(BookingState.FUTURE, booker.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findByBookerIdAndStartIsAfter(eq(booker.getId()), any(LocalDateTime.class), eq(page));
        // WAITING
        when(bookingRepository.findByBookerIdAndStatus(booker.getId(), BookingStatus.WAITING, page)).thenReturn(pageResult);
        result = bookingService.getAllBookings(BookingState.WAITING, booker.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findByBookerIdAndStatus(booker.getId(), BookingStatus.WAITING, page);
        // REJECTED
        when(bookingRepository.findByBookerIdAndStatus(booker.getId(), BookingStatus.REJECTED, page)).thenReturn(pageResult);
        result = bookingService.getAllBookings(BookingState.REJECTED, booker.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findByBookerIdAndStatus(booker.getId(), BookingStatus.REJECTED, page);
        // UNSUPPORTED_STATUS
        UnsupportedStatusException exception = assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getAllBookings(BookingState.UNSUPPORTED_STATUS, booker.getId(), 0, 20));
        assertEquals(UNSUPPORTED_STATUS, exception.getMessage());
        // ALL
        when(bookingRepository.findByBookerId(booker.getId(), page)).thenReturn(pageResult);
        result = bookingService.getAllBookings(BookingState.ALL, booker.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findByBookerId(booker.getId(), page);
    }

    @Test
    void findAllBookingsForOwnerTest() {
        Booking booking = Booking.builder().id(1L).item(item).booker(booker).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        Pageable page = PageRequest.of(DEFAULT_FROM_INT, DEFAULT_SIZE_INT, SORT_BY_START_DESC);
        Page<Booking> pageResult = new PageImpl<>(List.of(booking));
        // CURRENT
        when(bookingRepository.findBookingsByItemOwnerCurrent(eq(owner), any(LocalDateTime.class), eq(page))).thenReturn(pageResult);
        Collection<Booking> result = bookingService.getAllBookingsByOwner(BookingState.CURRENT, owner.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findBookingsByItemOwnerCurrent(eq(owner), any(LocalDateTime.class), eq(page));
        // PAST
        when(bookingRepository.findBookingByItemOwnerAndEndIsBefore(eq(owner), any(LocalDateTime.class), eq(page))).thenReturn(pageResult);
        result = bookingService.getAllBookingsByOwner(BookingState.PAST, owner.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findBookingByItemOwnerAndEndIsBefore(eq(owner), any(LocalDateTime.class), eq(page));
        // FUTURE
        when(bookingRepository.findBookingByItemOwnerAndStartIsAfter(eq(owner), any(LocalDateTime.class), eq(page))).thenReturn(pageResult);
        result = bookingService.getAllBookingsByOwner(BookingState.FUTURE, owner.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findBookingByItemOwnerAndStartIsAfter(eq(owner), any(LocalDateTime.class), eq(page));
        // WAITING
        when(bookingRepository.findBookingByItemOwnerAndStatus(owner, BookingStatus.WAITING, page)).thenReturn(pageResult);
        result = bookingService.getAllBookingsByOwner(BookingState.WAITING, owner.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findBookingByItemOwnerAndStatus(owner, BookingStatus.WAITING, page);
        // REJECTED
        when(bookingRepository.findBookingByItemOwnerAndStatus(owner, BookingStatus.REJECTED, page)).thenReturn(pageResult);
        result = bookingService.getAllBookingsByOwner(BookingState.REJECTED, owner.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findBookingByItemOwnerAndStatus(owner, BookingStatus.REJECTED, page);
        // UNSUPPORTED_STATUS
        UnsupportedStatusException exception = assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getAllBookingsByOwner(BookingState.UNSUPPORTED_STATUS, owner.getId(), 0, 20));
        assertEquals(UNSUPPORTED_STATUS, exception.getMessage());
        // ALL
        when(bookingRepository.findBookingByItemOwner(owner, page)).thenReturn(pageResult);
        result = bookingService.getAllBookingsByOwner(BookingState.ALL, owner.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findBookingByItemOwner(owner, page);
    }
}
