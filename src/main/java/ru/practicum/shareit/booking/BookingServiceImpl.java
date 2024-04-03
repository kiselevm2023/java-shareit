package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.mapToBookingItemDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private LocalDateTime timeNow = LocalDateTime.now();

    @Override
    public BookingDto createBooking(CreateBookingDto createBookingDto, long userId) {
        Optional<User> userOptional = userRepository.findByIdOrThrow(userId);
        Optional<Item> itemOptional = itemRepository.findByIdOrThrow(createBookingDto.getItemId());
        checkCorrectTime(createBookingDto.getStart(), createBookingDto.getEnd());
        validForAvailable(itemOptional);
        validForTime(itemOptional, createBookingDto);
        validForOwnerNotBookingMySelf(itemOptional, userId);
        User booker = userOptional.get();
        Item item = itemOptional.get();
        Booking booking = BookingMapper.toBooking(createBookingDto, booker, item);
        return BookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approvedBooking(long userId, long bookingId, boolean isApproved) {
        userRepository.searchByIdOrThrow(userId);
        Optional<Booking> bookingOptional = bookingRepository.findByIdOrThrow(bookingId);
        validFoundForBookerOrOwner(bookingOptional, userId);
        Booking booking = bookingOptional.get();
        validForStatus(booking);
        validForApproveBooker(booking, userId, isApproved);
        if (isApproved && booking.getItem().getOwner().getId() == userId) {
            booking.setStatus(Status.APPROVED);
        } else if (booking.getBooker().getId() == userId) {
            booking.setStatus(Status.CANCELED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        userRepository.searchByIdOrThrow(userId);
        Optional<Booking> booking = bookingRepository.findByIdOrThrow(bookingId);
        validFoundForBookingForOwner(booking, userId);
        return BookingMapper.bookingToDto(booking.get());
    }

    @Override
    public List<BookingDto> getAllBookingForUser(long userId, String state, int from, int size) {
        userRepository.searchByIdOrThrow(userId);
        State bookingState = State.checkState(state);
        switch (bookingState) {
            case ALL:
                return bookingRepository.findByBooker_IdOrderByStartDesc(userId, PageRequest.of(from, size)).stream()
                        .map(x -> BookingMapper.bookingToDto(x)).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findCurrentBookerForUser(userId, PageRequest.of(from, size)).stream()
                        .map(x -> BookingMapper.bookingToDto(x)).collect(Collectors.toList());
            case PAST:
                return bookingRepository.getPastBooking(userId, PageRequest.of(from, size)).stream()
                        .map(x -> BookingMapper.bookingToDto(x)).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository
                        .findByBooker_IdAndStartAfterOrderByStartDesc(userId, timeNow, PageRequest.of(from, size))
                        .stream().map(x -> BookingMapper.bookingToDto(x)).collect(Collectors.toList());

            case WAITING:
                return bookingRepository
                        .findByBooker_IdAndStatusOrderByStartDesc(userId, Status.WAITING, PageRequest.of(from, size))
                        .stream()
                        .map(x -> BookingMapper.bookingToDto(x))
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository
                        .findByBooker_IdAndStatusOrderByStartDesc(userId, Status.REJECTED, PageRequest.of(from, size))
                        .stream().map(x -> BookingMapper.bookingToDto(x))
                        .collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> getAllBookingForOwner(long userId, String state, int from, int size) {
        userRepository.searchByIdOrThrow(userId);
        State bookingState = State.checkState(state);
        switch (bookingState) {
            case ALL:
                return bookingRepository
                        .findByItem_Owner_IdOrderByStartDesc(userId, PageRequest.of(from, size)).stream()
                        .map(x -> BookingMapper.bookingToDto(x)).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository
                        .findCurrentBookerForOwner(userId, PageRequest.of(from, size)).stream()
                        .map(x -> BookingMapper.bookingToDto(x)).collect(Collectors.toList());
            case PAST:
                return bookingRepository
                        .findPastBookerForOwner(userId, PageRequest.of(from, size)).stream()
                        .map(x -> BookingMapper.bookingToDto(x)).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository
                        .findByItem_Owner_IdAndStartAfterOrderByStartDesc(userId, timeNow, PageRequest.of(from, size))
                        .stream().map(x -> BookingMapper.bookingToDto(x)).collect(Collectors.toList());
            case WAITING:
                return bookingRepository
                        .findByItem_Owner_IdAndStatusOrderByStartDesc(userId, Status.WAITING,
                                PageRequest.of(from, size))
                        .stream().map(x -> BookingMapper.bookingToDto(x)).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository
                        .findByItem_Owner_IdAndStatusOrderByStartDesc(userId, Status.REJECTED,
                                PageRequest.of(from, size))
                        .stream().map(x -> BookingMapper.bookingToDto(x)).collect(Collectors.toList());
            default:
                throw new ValidationException("Неверный статус");
        }
    }

    @Override
    public BookingItemDto getLastItemBooking(long itemId, long ownerId, LocalDateTime currentTime) {
        Booking booking = bookingRepository.getLastItemBooking(itemId, currentTime);
        return booking != null ? mapToBookingItemDto(booking) : null;
    }

    @Override
    public BookingItemDto getNextItemBooking(long itemId, long ownerId, LocalDateTime currentTime) {
        Booking booking = bookingRepository.getNextItemBooking(itemId, currentTime);
        return booking != null ? mapToBookingItemDto(booking) : null;
    }

    private void validFoundForBooking(Optional<Booking> booking) {
        if (booking.isEmpty()) {
            throw new NotFoundException("Бронирование не найдено");
        }
    }

    private void validFoundForBookingForOwner(Optional<Booking> booking, long userId) {
        if (!(booking.get().getBooker().getId() == userId || booking.get().getItem().getOwner().getId() == userId)) {
            throw new NotFoundException("Бронирование не найдено");
        }
    }

    private void validFoundForBookerOrOwner(Optional<Booking> booking, long userId) {
        if (!(booking.get().getBooker().getId() == userId || booking.get().getItem().getOwner().getId() == userId)) {
            throw new NotFoundException("Бронирование не найдено");
        }
    }

    private void validForAvailable(Optional<Item> itemOptional) {
        if (!itemOptional.get().getAvailable()) {
            throw new ValidationException("Вещь недоступна");
        }
    }

    private void validForTime(Optional<Item> itemOptional, CreateBookingDto createBookingDto) {
        List<Booking> bookings = bookingRepository.findBookingByItemToFree(itemOptional.get().getId(),
                createBookingDto.getStart(), createBookingDto.getEnd(), Sort.by(Sort.Direction.DESC, "start"));
        if (bookings.size() > 0) {
            throw new ValidationException("Вещь занята");
        }
    }

    private void checkCorrectTime(LocalDateTime start, LocalDateTime end) {
        if (!(start.isBefore(end) && start.isAfter(timeNow) && end.isAfter(timeNow))) {
            throw new ValidationException("Неверные параметры для времени, проверьте правильность запроса");
        }
    }

    private void validForStatus(Booking booking) {
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Статус у данного бронирования уже изменен");
        }
    }

    private void validForApproveBooker(Booking booking, long userId, boolean isApprove) {
        if (isApprove && booking.getBooker().getId() == userId) {
            throw new NotFoundException("Вы не можете одобрить данный запрос");
        }
    }

    private void validForOwnerNotBookingMySelf(Optional<Item> itemOptional, long userId) {
        if (itemOptional.get().getOwner().getId() == userId) {
            throw new NotFoundException("Вы не можете бронировать свою вещь");
        }
    }
}