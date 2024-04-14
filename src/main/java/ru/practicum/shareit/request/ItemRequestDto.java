package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validated.Create;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
public class ItemRequestDto {

    private Long id;

    @NotBlank(groups = Create.class, message = "Заполните описание запроса")
    private String description;

    private UserDto requestor;

    private LocalDateTime created;

    private List<ItemDto> items;
}