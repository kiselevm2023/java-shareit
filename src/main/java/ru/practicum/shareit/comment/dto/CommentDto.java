package ru.practicum.shareit.comment.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Data
public class CommentDto {

    private Long id;
    private String text;
    private Item item;
    private String authorName;
    private LocalDateTime created;
}