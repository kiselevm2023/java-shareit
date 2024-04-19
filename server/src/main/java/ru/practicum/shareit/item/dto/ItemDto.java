package ru.practicum.shareit.item.dto;

import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.request.ItemRequestDto;

import java.util.List;

@Getter
@Setter
@Builder
public class ItemDto {

    @Id
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

    private ItemRequestDto request;

    private List<CommentDto> comments;

}