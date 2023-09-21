package ru.practicum.shareit;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class TestUtils {
    public static final LocalDateTime now = LocalDateTime.now().plusHours(1);
    public static final User owner = User.builder().id(1L).name("owner").email("owner@mail.com").build();
    public static final User booker = User.builder().id(2L).name("booker").email("booker@mail.com").build();
    public static final User requester = User.builder().id(3L).name("requester").email("requestor@mail.com").build();
    public static final ItemRequest request = ItemRequest.builder().id(1L).requester(requester).description("description").created(LocalDateTime.now()).build();
    public static final Item item = Item.builder().id(1L).name("item").description("description").available(true).owner(owner).itemRequest(request).build();
    public static final Booking booking = Booking.builder().id(1L).item(item).start(now).end(now.plusDays(1)).booker(booker).status(BookingStatus.WAITING).build();

    public static final User ownerWithoutId = User.builder().name("owner").email("owner@mail.com").build();
    public static final User bookerWithoutId = User.builder().name("booker").email("booker@mail.com").build();
    public static final User requesterWithoutId = User.builder().name("requester").email("requestor@mail.com").build();
}
