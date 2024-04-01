package ru.practicum.shareit.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.dto.ResponseComment;
import ru.practicum.shareit.comment.dto.RequestComment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment toComment(ResponseComment commentDto, Item item, String name) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthorName(name);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static ResponseComment toCommentDto(Comment comment) {
        ResponseComment commentDto = new ResponseComment();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());

        ItemDto item = new ItemDto();
        item.setId(comment.getItem().getId());
        item.setName(comment.getItem().getName());
        commentDto.setItem(item);

        commentDto.setAuthorName(comment.getAuthorName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static List<ResponseComment> toCommentResponseDtoList(List<Comment> comments) {
        List<ResponseComment> commentResponseDtoList = new ArrayList<>();
        for (Comment comment : comments) {
            commentResponseDtoList.add(toCommentResponseDto(comment));
        }
        return commentResponseDtoList;
    }

    public static ResponseComment toCommentResponseDto(Comment comment) {
        ResponseComment commentResponseDto = new ResponseComment();
        commentResponseDto.setId(comment.getId());
        commentResponseDto.setText(comment.getText());
        commentResponseDto.setAuthorName(comment.getAuthorName());
        commentResponseDto.setCreated(comment.getCreated());
        ItemDto item = new ItemDto();
        item.setId(comment.getItem().getId());
        item.setName(comment.getItem().getName());
        commentResponseDto.setItem(item);
        return commentResponseDto;
    }

    public static Comment requestToComment(RequestComment commentDto, Item item, String name) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthorName(name);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}