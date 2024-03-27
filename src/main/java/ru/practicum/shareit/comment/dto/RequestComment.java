package ru.practicum.shareit.comment.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Data
public class RequestComment {

    private Long id;
    @NotBlank(message = "Текст комметария не может быть пустым")
    @Size(max = 255, message = "Длина текста комментария должна быть не больше {max} символов")
    private String text;
}