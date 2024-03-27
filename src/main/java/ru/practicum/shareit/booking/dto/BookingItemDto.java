package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

@Data
@RequiredArgsConstructor
public class BookingItemDto extends BookingDto{
    private long id;
    private long bookerId;
}
