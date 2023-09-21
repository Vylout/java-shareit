package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = :user_id " +
            "AND b.start < :time " +
            "AND b.end > :time")
    Page<Booking> findByBookerIdCurrent(@Param("user_id") Long userId, @Param("time") LocalDateTime now, Pageable page);

    Page<Booking> findByBookerIdAndEndIsBefore(Long userId, LocalDateTime now, Pageable page);

    Page<Booking> findByBookerIdAndStartIsAfter(Long userId, LocalDateTime now, Pageable page);

    Page<Booking> findByBookerIdAndStatus(Long userId, BookingStatus bookingStatus, Pageable page);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner = :user " +
            "AND b.start < :time " +
            "AND b.end > :time")
    Page<Booking> findBookingsByItemOwnerCurrent(@Param("user") User owner, @Param("time") LocalDateTime now, Pageable page);

    Page<Booking> findBookingByItemOwnerAndEndIsBefore(User owner, LocalDateTime now, Pageable page);

    Page<Booking> findBookingByItemOwnerAndStartIsAfter(User owner, LocalDateTime now, Pageable page);

    Page<Booking> findBookingByItemOwnerAndStatus(User owner, BookingStatus status, Pageable page);

    Page<Booking> findBookingByItemOwner(User owner, Pageable page);

    Page<Booking> findByBookerId(Long userId, Pageable page);

    List<Booking> findBookingByItemIdAndStartBefore(Long itemId, LocalDateTime now, Sort sort);

    List<Booking> findBookingByItemIdAndStartAfterAndStatus(Long itemId, LocalDateTime now, BookingStatus status, Sort sort);

    List<Booking> findBookingByItemInAndStatus(Collection<Item> items, BookingStatus status);

    List<Booking> findBookingByItemIdAndBookerIdAndStatusAndStartBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime now);
}