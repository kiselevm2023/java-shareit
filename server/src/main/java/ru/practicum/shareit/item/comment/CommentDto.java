package ru.practicum.shareit.item.comment;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validated.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;

    @NotBlank(groups = Create.class, message = "Комментарий не может быть пустым")
    private String text;

    private UserDto author;

    private String authorName;

    private ItemDto item;

    private LocalDateTime created;
}