package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    default Optional<Item>  findByIdOrThrow(long itemId) {
        return findById(itemId).map(Optional::of).orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
    }

    default Item searchByIdOrThrow(long itemId) {
        return findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена или ошибка доступа"));
    }

    default Item findItemByIdAndOwnerIdOrThrow(long idItem, long idOwner) {
        return findItemByIdAndOwnerId(idItem, idOwner).orElseThrow(() -> new NotFoundException("Вещь не найдена или ошибка доступа"));
    }

    default Item findByIdAndOwnerIdNotOrThrow(long itemId, long bookerId) {
        return findByIdAndOwnerIdNot(itemId, bookerId).orElseThrow(() -> new NotFoundException("Вещь доступная для бронирования не найдена"));
    }

    @Query(" select i from Item i " +
            "where i.available = true " +
            "and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')))")
    Page<Item> search(String text, Pageable pageable);

    Page<Item> findAllItemsByOwnerId(Long ownerId, Pageable pageable);

    Optional<Item> findItemByIdAndOwnerId(Long itemId, Long ownerId);

    Optional<Item> findByIdAndOwnerIdNot(Long itemId, Long ownerId);

    List<Item> findAllItemsByRequestId(Long requestId);

    List<Item> findByRequestIn(List<ItemRequest> requests, Sort sort);
}