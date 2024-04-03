package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;


import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderByIdAsc(long ownerId);

    default Optional<Item>  findByIdOrThrow(long itemId) {
        return findById(itemId).map(Optional::of).orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
    }

    default Item searchByIdOrThrow(long itemId) {
        return findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
    }

    List<Item> findByOwner_id(long userId);

    @Query(value = "SELECT i FROM Item AS i WHERE ((UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%'))) AND i.available IS TRUE)")
    List<Item> getItemForBooker(String text, Pageable pageable);

    Page<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query(value = "SELECT i FROM Item AS i WHERE i.requestId = ?1")
    List<Item> findByRequestId(long requestId);
}