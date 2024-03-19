package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private long id;
    @NotBlank(message = "поле name пустое")
    private String name;
    @NotBlank(message = "описание пустое")
    private String description;
    @AssertTrue(message = "поле available пустое")
    private boolean available;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
}