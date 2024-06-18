package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.status.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@Validated
@Builder
public class BookingNextLastDto {

    @Id
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    private ItemDto item;

    private UserDto booker;

    private Long bookerId;

    private BookingStatus status;
}