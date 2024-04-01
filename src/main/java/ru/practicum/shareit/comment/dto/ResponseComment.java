package ru.practicum.shareit.comment.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

import ru.practicum.shareit.item.dto.ItemDto;

@RequiredArgsConstructor
@Data
public class ResponseComment {

    private Long id;
    private String text;
    private ItemDto item;
    private String authorName;
    private LocalDateTime created;
}