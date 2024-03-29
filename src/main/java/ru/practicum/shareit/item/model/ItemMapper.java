package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.comment.dto.ResponseComment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static Item createToItem(CreateItemDto createItemDto, User user) {
        Item item = new Item();
        item.setName(createItemDto.getName());
        item.setDescription(createItemDto.getDescription());
        item.setAvailable(createItemDto.getAvailable());
        item.setOwner(user);
        return item;
    }

    public static ItemDto mapToItemDto(Item item, BookingItemDto lastBooking, BookingItemDto nextBooking, List<ResponseComment> commentsDto) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(commentsDto);
        return itemDto;
    }

    public static ItemOwnerDto toItemResponseDtoWithBookings(Item item) {
        ItemOwnerDto itemDtoWithBookings = new ItemOwnerDto();
        itemDtoWithBookings.setId(item.getId());
        itemDtoWithBookings.setName(item.getName());
        itemDtoWithBookings.setDescription(item.getDescription());
        itemDtoWithBookings.setAvailable(item.getAvailable());
        return itemDtoWithBookings;
    }

    public static ItemOwnerDto toItemResponseDtoWithBookings(Item item, List<ResponseComment> comments) {
        ItemOwnerDto  itemDtoWithBookings = new ItemOwnerDto();
        itemDtoWithBookings.setId(item.getId());
        itemDtoWithBookings.setName(item.getName());
        itemDtoWithBookings.setDescription(item.getDescription());
        itemDtoWithBookings.setAvailable(item.getAvailable());
        itemDtoWithBookings.setComments(comments);
        return itemDtoWithBookings;
    }
}