package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.BookingNextLastDto;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.persistence.Id;
import java.util.List;

@Getter
@Setter

@Validated
@Builder
public class ItemWithBookingsDateDto {

    @Id
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingNextLastDto lastBooking;

    private BookingNextLastDto nextBooking;

    private List<CommentDto> comments;

}