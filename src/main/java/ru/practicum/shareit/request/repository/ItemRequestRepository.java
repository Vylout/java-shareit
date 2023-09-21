package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findRequestByRequesterIdOrderByCreatedDesc(Long requester);
    @Query("SELECT r " +
            "FROM ItemRequest r " +
            "WHERE r.requester.id <> :user_id")
    Page<ItemRequest> findAllForUser(@Param("user_id") Long userId, Pageable pageable);
}
