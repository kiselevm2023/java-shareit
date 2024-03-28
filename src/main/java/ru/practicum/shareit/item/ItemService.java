package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.comment.dto.RequestComment;

import java.util.List;

public interface ItemService {

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getAllItemForOwner(long userId);

    List<ItemDto> getItemForBooker(String text, long userId);

    ItemDto createItem(Long userId, CreateItemDto createItemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

    Comment createComment(long userId, RequestComment commentDto, long itemId);
}