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
    @NotBlank(message = "field name is empty")
    private String name;
    @NotBlank(message = "description is empty")
    private String description;
    @NotNull(message = "поле available пустое")
    private Boolean available;

}
