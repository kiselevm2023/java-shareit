package ru.practicum.shareit.items.commetDto;

import lombok.*;
import ru.practicum.shareit.validated.Create;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @NotBlank(groups = {Create.class}, message = "Комментарий не может быть пустым")
    private String text;
}