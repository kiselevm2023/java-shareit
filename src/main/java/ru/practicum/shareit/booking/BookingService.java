package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingDto createBooking(CreateBookingDto createBookingDto, long userId);

    BookingDto approvedBooking(long userId, long bookingId, boolean isApproved);

    BookingDto getBookingById(long userId, long bookingId);

    List<BookingDto> getAllBookingForUser(long userId, String state, int from, int size);

    List<BookingDto> getAllBookingForOwner(long userId, String state, int from, int size);

    BookingItemDto getLastItemBooking(long itemId, long ownerId, LocalDateTime currentTime);

    BookingItemDto getNextItemBooking(long itemId, long ownerId, LocalDateTime currentTime);
}