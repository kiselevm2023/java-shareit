package ru.practicum.shareit.item.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.item.dto.ItemMapper;

@Component
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment, UserMapper userMapper, ItemMapper itemMapper) {

        CommentDto commentDto = new CommentDto(
            comment.getId(),
            comment.getText(),
            userMapper.toUserDto(comment.getAuthor()),
            comment.getAuthor().getName(),
            itemMapper.toItemDto(comment.getItem()),
            comment.getCreated()
        );
        return commentDto;
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> listComments, UserMapper userMapper, ItemMapper itemMapper) {
        List<CommentDto> listCommentsDto = new ArrayList<>();

        for (Comment comment : listComments) {
            listCommentsDto.add(toCommentDto(comment, userMapper, itemMapper));
        }

        return listCommentsDto;
    }

    public static Comment toComment(CommentDto commentDto, User user, Item item) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                user,
                item,
                LocalDateTime.now());
    }
}