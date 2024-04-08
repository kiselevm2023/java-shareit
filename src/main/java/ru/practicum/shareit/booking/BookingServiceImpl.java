package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.customPageRequest.CustomPageRequest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.status.BookingStatus;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingDto createBooking(Long bookerId, BookingDto bookingDto) {

        User booker = userRepository.searchByIdOrThrow(bookerId);

        Item item = itemRepository.findByIdAndOwnerIdNotOrThrow(bookingDto.getItemId(), bookerId);

        if (item.getAvailable() == false) {
            throw new BadRequestException("Вещь недоступна");
        }

        return bookingMapper.toBookingDto(bookingRepository
                .save(bookingMapper.toBooking(bookingDto, item, booker, BookingStatus.WAITING)));
    }

    @Transactional
    public BookingDto approvingBooking(Long bookingId, Long ownerId, Boolean approved) {

        Booking booking = bookingRepository.findByIdAndOwnerIdNotOrThrow(bookingId, ownerId);

        BookingStatus status = booking.getStatus();
        if (BookingStatus.APPROVED.equals(status) || BookingStatus.REJECTED.equals(status)) {
            throw new BadRequestException("Вы уже меняли статус");
        }

        booking.setStatus(approved == true ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toBookingDto(booking);
    }

    public BookingDto getBookingById(Long bookingId, Long userId) {

        User user = userRepository.searchByIdOrThrow(userId);

        Booking booking = bookingRepository.findBookingByBookingIdAndOwnerIdOrOwnerItemId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Ничего не найдено"));

        return bookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> findUserBookingsWithState(Long userId, String status, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new BadRequestException("Неверные параметры пагинации");
        }

        User user = userRepository.searchByIdOrThrow(userId);

        Pageable pageable = CustomPageRequest.customOf(from, size);

        return bookingMapper.toBookingDto(bookingRepository
                .findUserBookingsWithState(userId, mapToStateString(status), pageable).getContent());
    }

    public List<BookingDto> findOwnerBookingsWithState(Long ownerId, String status, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new BadRequestException("Неверные параметры пагинации");
        }

        User user = userRepository.searchByIdOrThrow(ownerId);

        Pageable pageable = CustomPageRequest.customOf(from, size);

        return bookingMapper.toBookingDto(bookingRepository
                .findOwnerBookingsWithState(ownerId, mapToStateString(status), pageable).getContent());
    }

    public String mapToStateString(String state) {
        switch (state.toUpperCase()) {
            case "CURRENT":
                return "CURRENT";
            case "PAST":
                return "PAST";
            case "FUTURE":
                return "FUTURE";
            case "WAITING":
                return "WAITING";
            case "REJECTED":
                return "REJECTED";
            case "ALL":
                return "ALL";
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }

    @Override
    public BookingNextLastDto getNextItemBooking(long itemId, long ownerId, LocalDateTime currentTime) {
        Booking booking = bookingRepository.getNextItemBooking(itemId, currentTime);
        return booking != null ? bookingMapper.toBookingLastNextDto(booking) : null;
    }

    @Override
    public BookingNextLastDto getLastItemBooking(long itemId, long ownerId, LocalDateTime currentTime) {
        Booking booking = bookingRepository.getLastItemBooking(itemId, currentTime);
        return booking != null ? bookingMapper.toBookingLastNextDto(booking) : null;
    }

    private void validFoundForBooking(Optional<Booking> booking) {
        if (booking.isEmpty()) {
            throw new NotFoundException("Бронирование не найдено");
        }
    }
}