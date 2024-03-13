package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item createItem(Long userId, Item item);

    Item updateItem(long userId, Item item, long itemId);

    List<Item> getAllItemForOwner(Long userId);

    List<Item> searchItem(String text);

    Item getItemById(Long itemId);
}