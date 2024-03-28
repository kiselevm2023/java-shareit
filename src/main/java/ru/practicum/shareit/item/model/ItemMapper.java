package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.comment.dto.ResponseComment;
import ru.practicum.shareit.item.dto.ItemCommentDto;
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

    public static Item toItem(ItemDto itemDto, User user) {
        Item item = new Item();
        if (itemDto.getId() != 0) {
            item.setId(itemDto.getId());
        }
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;
    }

    /*public static ItemCommentDto toItemCommentDto(ItemOwnerDto itemOwnerDto, List<CommentDto> comments) {
        ItemCommentDto itemCommentDto = new ItemCommentDto();
        itemCommentDto.setComments(comments);
        itemCommentDto.setDescription(itemOwnerDto.getDescription());
        itemCommentDto.setAvailable(itemOwnerDto.getAvailable());
        itemCommentDto.setId(itemOwnerDto.getId());
        itemCommentDto.setLastBooking(itemOwnerDto.getLastBooking());
        itemCommentDto.setNextBooking(itemOwnerDto.getNextBooking());
        itemCommentDto.setName(itemOwnerDto.getName());
        return itemCommentDto;
    }*/

    /*public static ItemCommentDto toItemCommentDto(ItemDto itemDto, List<CommentDto> comments) {
        ItemCommentDto itemCommentDto = new ItemCommentDto();
        itemCommentDto.setComments(comments);
        itemCommentDto.setDescription(itemDto.getDescription());
        itemCommentDto.setAvailable(itemDto.getAvailable());
        itemCommentDto.setId(itemDto.getId());
        itemCommentDto.setLastBooking(itemDto.getLastBooking());
        itemCommentDto.setNextBooking(itemDto.getNextBooking());
        itemCommentDto.setName(itemDto.getName());
        return itemCommentDto;
    }*/

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