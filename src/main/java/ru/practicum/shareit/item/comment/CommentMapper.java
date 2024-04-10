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

    /* private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Autowired
    public CommentMapper(UserMapper userMapper, ItemMapper itemMapper) {
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
    }   */

    /* public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor(),
                comment.getAuthor().getName(),
                comment.getItem(),
                comment.getCreated()
        );
    }  */

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
    /*

    public Comment toComment(CommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                commentDto.getAuthor(),
                commentDto.getItem(),
                null);
    }   */

    public static Comment toComment(CommentDto commentDto, User user, Item item) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                user, // вместо commentDto.getAuthor()
                item, // вместо commentDto.getItem()
                LocalDateTime.now()); // Установка текущего времени, если это требуется для логики создания
    }
}