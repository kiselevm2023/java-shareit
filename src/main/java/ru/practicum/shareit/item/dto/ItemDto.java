package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private long id;
    @NotBlank(message = "поле name пустое")
    private String name;
    @NotBlank(message = "описание пустое")
    private String description;
    @NotNull(message = "поле available пустое")
    private Boolean available;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
}