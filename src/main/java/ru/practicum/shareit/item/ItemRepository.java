package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderByIdAsc(long ownerId);

    default Item findByIdOrThrow(Long itemId) {
        return findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
    }

    List<Item> findByOwner_id(long userId);

    @Query(value = "SELECT i FROM Item AS i WHERE ((UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
                "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%'))) AND i.available IS TRUE)")
    List<Item> getItemForBooker(String text);

    List<Item> findAllByOwnerId(Long ownerId, Sort sort);

}