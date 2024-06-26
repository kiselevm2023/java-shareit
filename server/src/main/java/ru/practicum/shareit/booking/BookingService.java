package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long id, BookingDto bookingDto);

    BookingDto approvingBooking(Long ownerId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> findUserBookingsWithState(Long userId, String status, Integer from, Integer size);

    List<BookingDto> findOwnerBookingsWithState(Long ownerId, String status, Integer from, Integer size);

    BookingNextLastDto getNextItemBooking(long itemId, long ownerId, LocalDateTime currentTime);

    BookingNextLastDto getLastItemBooking(long itemId, long ownerId, LocalDateTime currentTime);

}