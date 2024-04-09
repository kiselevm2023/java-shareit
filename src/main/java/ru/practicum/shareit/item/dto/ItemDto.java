package ru.practicum.shareit.item.dto;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.validated.Create;

import java.util.List;


@Data
@Builder
public class ItemDto {

    @Id
    private Long id;

    @NotBlank(groups = {Create.class}, message = "Name не может быть пустым")
    private String name;

    @NotBlank(groups = {Create.class}, message = "Description не может быть пустым")
    private String description;

    @NotNull(groups = {Create.class}, message = "Available не может быть пустым")
    private Boolean available;

    private Long requestId;

    private ItemRequestDto request;

    private List<CommentDto> comments;

}