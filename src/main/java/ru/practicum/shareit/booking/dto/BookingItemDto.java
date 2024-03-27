package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BookingItemDto extends BookingDto {
    private long id;
    private long bookerId;
}
