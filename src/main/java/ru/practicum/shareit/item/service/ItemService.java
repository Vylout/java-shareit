package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ElementNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Slf4j
@Service
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserRepository userRepository;

    @Autowired
    public ItemService(ItemStorage itemStorage, UserRepository userRepository) {
        this.itemStorage = itemStorage;
        this.userRepository = userRepository;
    }

    public Map<Long, Item> getAllItems() {
        return itemStorage.getAllItems();
    }

    public Item getItemById(Long id) {
        if (!getAllItems().containsKey(id)) {
            log.error("Вещи с таким ID {} не найдено.", id);
            throw new ElementNotFoundException("Вещ с ID " + id);
        }
        return itemStorage.getItemById(id);
    }

    public Item addItem(Long userId, ItemDto itemDto) {
        checkItemDto(itemDto);
        User user = getUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return itemStorage.addItem(item);
    }

    public Item updateItem(Item item, Long ownerId) {
        checkTheItemOwner(item.getId(), ownerId);
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
        itemStorage.updateItem(updateItem);
        return updateItem;
    }

    public List<Item> getAllItemsByUser(Long userId) {
        return itemStorage.findAllByOwnerOrderById(userId);
    }

    public Collection<Item> findItemByText(String text) {
        List<Item> itemsList = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return itemsList;
        }
        for (Item item : getAllItems().values()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                if (item.getAvailable().equals(true)) {
                    itemsList.add(item);
                }
            }
        }
        return itemsList;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ElementNotFoundException(String.format("Пользователь с ID " + userId)));
    }

    private void checkItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.error("Имя пользователя указано пустым.");
            throw new ValidationException("Имя пользователя не указано.");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.error("Описание товара не указано.");
            throw new ValidationException("Не указано описание товара.");
        }
        if (itemDto.getAvailable() == null) {
            log.error("Статус заказа не указан.");
            throw new ValidationException("Статус заказа не указан.");
        }
    }

    private void checkTheItemOwner(Long itemId, Long ownerId) {
        if (!ownerId.equals(getItemById(itemId).getOwner().getId())) {
            log.error("Указан не верный владелец вещи.");
            throw new ElementNotFoundException("Владелец вещи");
        }
    }
}
