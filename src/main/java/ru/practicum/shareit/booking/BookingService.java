package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(CreateBookingDto createBookingDto, long userId);

    BookingDto approvedBooking(long userId, long bookingId, boolean isApproved);

    BookingDto getBookingById(long userId, long bookingId);

    List<BookingDto> getAllBookingForUser(long userId, String state);

    List<BookingDto> getAllBookingForOwner(long userId, String state);
}