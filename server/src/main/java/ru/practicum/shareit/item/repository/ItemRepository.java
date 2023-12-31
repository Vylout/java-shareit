package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE upper(i.name) LIKE upper(concat('%', :text, '%')) " +
            "OR upper(i.description) LIKE upper(concat('%', :text, '%')) " +
            "AND i.available = true")
    List<Item> search(@Param("text") String text, Pageable page);

    Page<Item> findAllByOwnerOrderById(User owner, Pageable page);

    @Query("SELECT i " +
            "FROM Item AS i " +
            "WHERE i.itemRequest in ?1")
    List<Item> findAllByRequestIdIn(List<ItemRequest> requests);

    List<Item> findAllByItemRequest(ItemRequest request);
}
