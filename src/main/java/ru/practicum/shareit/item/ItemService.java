package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

    List<ItemDto> getAllItemForOwner(Long userId);

    List<ItemDto> searchItem(String text);

    ItemDto getItemById(Long itemId);
}
