package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto bookingToDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setItem(booking.getItem());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStart(booking.getStart());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        if (bookingDto.getId() != 0) {
            booking.setId(bookingDto.getId());
        }
        booking.setBooker(bookingDto.getBooker());
        booking.setItem(bookingDto.getItem());
        booking.setEnd(bookingDto.getEnd());
        booking.setStart(bookingDto.getStart());
        booking.setStatus(Status.WAITING);
        return booking;
    }
}