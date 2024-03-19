package ru.practicum.shareit.item.model;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User user) {
        Item item = new Item();
        if (itemDto.getId() != 0) {
            item.setId(itemDto.getId());
        }
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.isAvailable());
        item.setOwner(user);
        return item;
    }

    public static ItemCommentDto toItemCommentDto(ItemOwnerDto itemOwnerDto, List<CommentDto> comments) {
        ItemCommentDto itemCommentDto = new ItemCommentDto();
        itemCommentDto.setComments(comments);
        itemCommentDto.setDescription(itemOwnerDto.getDescription());
        itemCommentDto.setAvailable(itemOwnerDto.isAvailable());
        itemCommentDto.setId(itemOwnerDto.getId());
        itemCommentDto.setLastBooking(itemOwnerDto.getLastBooking());
        itemCommentDto.setNextBooking(itemOwnerDto.getNextBooking());
        itemCommentDto.setName(itemOwnerDto.getName());
        return itemCommentDto;
    }

    public static ItemCommentDto toItemCommentDto(ItemDto itemDto, List<CommentDto> comments) {
        ItemCommentDto itemCommentDto = new ItemCommentDto();
        itemCommentDto.setComments(comments);
        itemCommentDto.setDescription(itemDto.getDescription());
        itemCommentDto.setAvailable(itemDto.isAvailable());
        itemCommentDto.setId(itemDto.getId());
        itemCommentDto.setLastBooking(itemDto.getLastBooking());
        itemCommentDto.setNextBooking(itemDto.getNextBooking());
        itemCommentDto.setName(itemDto.getName());
        return itemCommentDto;
    }

    public static ItemOwnerDto toItemOwnerDto(Item item, List<Booking> bookings) {

        ItemOwnerDto itemOwnerDto = new ItemOwnerDto();
        itemOwnerDto.setId(item.getId());
        itemOwnerDto.setName(item.getName());
        itemOwnerDto.setDescription(item.getDescription());
        itemOwnerDto.setAvailable(item.isAvailable());

        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking1 -> booking1.getEnd().isBefore(LocalDateTime.now())
                        || (booking1.getStart().isBefore(LocalDateTime.now()) &&
                        booking1.getEnd().isAfter(LocalDateTime.now())))
                .max(Comparator.comparing(Booking::getEnd));
        if (lastBooking.isPresent()) {
            BookingItemDto bookingItemDto = new BookingItemDto();
            bookingItemDto.setBookerId(lastBooking.get().getBooker().getId());
            bookingItemDto.setBooking(lastBooking.get());
            bookingItemDto.setId(lastBooking.get().getId());
            itemOwnerDto.setLastBooking(bookingItemDto);
        } else {
            itemOwnerDto.setLastBooking(null);
        }

        Optional<Booking> nextBooking = bookings.stream()
                .filter(booking1 -> booking1.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart));
        if (nextBooking.isPresent()) {
            BookingItemDto bookingItemDto = new BookingItemDto();
            bookingItemDto.setBookerId(nextBooking.get().getBooker().getId());
            bookingItemDto.setBooking(nextBooking.get());
            bookingItemDto.setId(nextBooking.get().getId());
            itemOwnerDto.setNextBooking(bookingItemDto);
        } else {
            itemOwnerDto.setNextBooking(null);
        }
        return itemOwnerDto;
    }

}