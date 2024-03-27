package ru.practicum.shareit.comment.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Data
public class ResponseComment {

    private Long id;
    private String text;
    private Item item;
    private String authorName;
    private LocalDateTime created;
}