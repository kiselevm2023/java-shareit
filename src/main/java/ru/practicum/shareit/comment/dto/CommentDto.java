package ru.practicum.shareit.comment.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Data
public class CommentDto {

    private Long id;
    @NotBlank(message = "Текст комметария не может быть пустым")
    @Size(max = 255, message = "Длина текста комментария должна быть не больше {max} символов")
    private String text;
    private ItemDto item;
    @Size(max = 255, message = "Длина имени автора должна быть не больше {max} символов")
    private String authorName;
    private LocalDateTime created;
}