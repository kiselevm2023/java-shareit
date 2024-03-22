package ru.practicum.shareit.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment toComment(CommentDto commentDto, Item item, String name) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthorName(name);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItem(comment.getItem());
        commentDto.setAuthorName(comment.getAuthorName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }
}