package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.status.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validated.Create;

import javax.persistence.Id;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Validated
@Builder
public class BookingNextLastDto {

    @Id
    private Long id;

    @NotNull(groups = {Create.class}, message = "Start не может быть пустым")
    @FutureOrPresent(groups = {Create.class}, message = "Нельзя бронировать задним числом")
    private LocalDateTime start;

    @NotNull(groups = {Create.class}, message = "End не может быть пустым")
    @FutureOrPresent(groups = {Create.class}, message = "Нельзя бронировать задним числом")
    private LocalDateTime end;

    @NotNull(groups = {Create.class}, message = "ItemId не может быть пустым")
    private Long itemId;

    private ItemDto item;

    private UserDto booker;

    private Long bookerId;

    private BookingStatus status;
}