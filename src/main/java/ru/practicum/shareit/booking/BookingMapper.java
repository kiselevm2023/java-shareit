package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto bookingToDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());

        UserDto booker = new UserDto();
        booker.setId(booking.getBooker().getId());
        bookingDto.setBooker(booker);

        ItemDto item = new ItemDto();
        item.setId(booking.getItem().getId());
        item.setName(booking.getItem().getName());
        bookingDto.setItem(item);

        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStart(booking.getStart());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static Booking toBooking(CreateBookingDto createBookingDto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setEnd(createBookingDto.getEnd());
        booking.setStart(createBookingDto.getStart());
        booking.setStatus(Status.WAITING);
        return booking;
    }

    public static BookingItemDto mapToBookingItemDto(Booking booking) {
        BookingItemDto bookingResponseDto = new BookingItemDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setBookerId(booking.getBooker().getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setItemId(booking.getItem().getId());

        UserDto booker = new UserDto();
        booker.setId(booking.getBooker().getId());
        bookingResponseDto.setBooker(booker);

        ItemDto item = new ItemDto();
        item.setId(booking.getItem().getId());
        item.setName(booking.getItem().getName());
        bookingResponseDto.setItem(item);

        bookingResponseDto.setStatus(booking.getStatus());
        return bookingResponseDto;
    }

    public static BookingItemDto toBookingResponseDto(Booking booking) {
        BookingItemDto bookingResponseDto = new BookingItemDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setBookerId(booking.getBooker().getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setItemId(booking.getItem().getId());

        UserDto booker = new UserDto();
        booker.setId(booking.getBooker().getId());
        bookingResponseDto.setBooker(booker);

        ItemDto item = new ItemDto();
        item.setId(booking.getItem().getId());
        item.setName(booking.getItem().getName());
        bookingResponseDto.setItem(item);

        bookingResponseDto.setStatus(booking.getStatus());
        return bookingResponseDto;
    }
}