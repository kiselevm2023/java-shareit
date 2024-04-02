package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CreateItemDto {

    private long id;
    @NotBlank(groups = {Check.OnCreate.class}, message = "поле name пустое")
    @Size(groups = {Check.OnCreate.class, Check.OnUpdate.class}, max = 255, message = "Длина имени должна быть не больше {max} символов")
    private String name;
    @NotBlank(groups = {Check.OnCreate.class}, message = "описание пустое")
    @Size(groups = {Check.OnCreate.class, Check.OnUpdate.class}, max = 255, message = "Длина описания должна быть не больше {max} символов")
    private String description;
    @NotNull(groups = {Check.OnCreate.class}, message = "поле available пустое")
    private Boolean available;
}