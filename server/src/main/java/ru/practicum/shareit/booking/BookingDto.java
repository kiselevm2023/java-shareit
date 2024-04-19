package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.status.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;

@Getter
@Setter
@Builder
public class BookingDto {

    @Id
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    private ItemDto item;

    private UserDto booker;

    private BookingStatus status;

    public boolean isEndDateAfterStartDate() {
        if (end == null || start == null || end.isBefore(start) || end.isEqual(start)) {
            throw new BadRequestException("Ошибка в датах");
        }
        return true;
    }
}