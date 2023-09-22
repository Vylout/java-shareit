package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.ElementNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utils.Constants.SORT_BY_CREATED_DESC;
import static ru.practicum.shareit.utils.ValidationErrors.USER_NOT_FOUND;

@Service
@Transactional
public class ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemRequestService(UserRepository userRepository, ItemRepository itemRepository, ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    public ItemRequest addItemRequest(ItemRequest itemRequest, Long requesterId) {
        User user = getUser(requesterId);
        itemRequest.setRequester(user);
        return itemRequestRepository.save(itemRequest);
    }

    public List<ResponseItemRequestDto> getAllItemRequestsByOwner(Long requesterId) {
        getUser(requesterId);
        List<ItemRequest> itemRequests = itemRequestRepository.findRequestByRequesterIdOrderByCreatedDesc(requesterId);
        Map<ItemRequest, List<Item>> itemByRequests = findItemsByRequests(itemRequests);
        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.toResponseItemRequestDto(itemRequest, itemByRequests.get(itemRequest)))
                .collect(Collectors.toList());
    }

    public List<ResponseItemRequestDto> getAll(Long requesterId, int from, int size) {
        getUser(requesterId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllForUser(requesterId, PageRequest.of(from, size, SORT_BY_CREATED_DESC)).toList();
        Map<ItemRequest, List<Item>> itemByRequests = findItemsByRequests(itemRequests);
        return itemByRequests.entrySet().stream()
                .map(entry -> ItemRequestMapper.toResponseItemRequestDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public ResponseItemRequestDto getById(Long requesterId, Long userId) {
        getUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requesterId).orElseThrow(() ->
                new ElementNotFoundException(USER_NOT_FOUND));
        List<Item> items = itemRepository.findAllByItemRequest(itemRequest);
        return ItemRequestMapper.toResponseItemRequestDto(itemRequest, items);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ElementNotFoundException(USER_NOT_FOUND));
    }

    private Map<ItemRequest, List<Item>> findItemsByRequests(List<ItemRequest> requests) {
        return itemRepository.findAllByRequestIdIn(requests)
                .stream()
                .collect(Collectors.groupingBy(Item::getItemRequest, Collectors.toList()));
    }
}
