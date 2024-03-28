package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.comment.dto.ResponseComment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private long id;
    @NotBlank(message = "поле name пустое")
    @Size(max = 255, message = "Длина имени должна быть не больше {max} символов")
    private String name;
    @NotBlank(message = "описание пустое")
    @Size(max = 255, message = "Длина описания должна быть не больше {max} символов")
    private String description;
    @NotNull(message = "поле available пустое")
    private Boolean available;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private List<ResponseComment> comments;
}