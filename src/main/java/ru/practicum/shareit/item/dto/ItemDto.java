package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.comment.dto.ResponseComment;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private List<ResponseComment> comments;
}