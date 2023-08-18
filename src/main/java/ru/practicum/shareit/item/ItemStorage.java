package ru.practicum.shareit.item;

import java.util.List;
import java.util.Map;

public interface ItemStorage {

    Map<Long, Item> getAllItems();

    Item getItemById(Long id);

    Item addItem(Item item);

    Item updateItem(Item item);

    List<Item> findAllByOwnerOrderById(Long userId);
}
