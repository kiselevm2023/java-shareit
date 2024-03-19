package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class ItemCommentDto extends ItemDto {
    private List<CommentDto> comments;
}