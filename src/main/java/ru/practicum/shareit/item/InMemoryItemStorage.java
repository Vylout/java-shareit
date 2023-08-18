package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    private long id = 1;

    public Map<Long, Item> getAllItems() {
        return items;
    }

    public Item getItemById(Long id) {
        return items.get(id);
    }

    @Override
    public Item addItem(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        log.warn("Данные вещи с ID {} обновлены.", item.getId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> findAllByOwnerOrderById(Long userId) {
        List<Item> itemsList = new ArrayList<>();
        for (Item item : getAllItems().values()) {
            if (item.getOwner().getId().equals(userId)) {
                itemsList.add(item);
            }
        }
        return itemsList;
    }

    private Long generateId() {
        return id++;
    }
}
