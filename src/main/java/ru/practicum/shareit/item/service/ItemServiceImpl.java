package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.customPageRequest.CustomPageRequest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDateDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.status.BookingStatus;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final RequestRepository requestRepository;
    private final BookingService bookingService;
    @Override
    public ItemWithBookingsDateDto getItemById(Long itemId, Long ownerId) {
        User user = userRepository.searchByIdOrThrow(ownerId);
        Item item = itemRepository.searchByIdOrThrow(itemId);
        return getItemDtoWithBookingAndComments(item, ownerId);
    }

    private ItemWithBookingsDateDto getItemDtoWithBookingAndComments(Item item, long ownerId) {
        List<CommentDto> commentsDto = commentRepository.findAllCommentByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (item.getOwner().getId() != ownerId) {
            return itemMapper.toItemWithBookingDto(item, null, null, commentsDto);
        }
        LocalDateTime currentTime = LocalDateTime.now();
        BookingNextLastDto lastBooking = bookingService.getLastItemBooking(item.getId(), ownerId, currentTime);
        BookingNextLastDto nextBooking = bookingService.getNextItemBooking(item.getId(), ownerId, currentTime);
        return itemMapper.toItemWithBookingDto(item, lastBooking, nextBooking, commentsDto == null ? List.of() : commentsDto);
    }
    @Override
    public List<ItemWithBookingsDateDto> getAllItemsByOwnerId(Long id, Integer from, Integer size) {

        userRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id %d не найден", id)));
        PageRequest pageRequest = PageRequest.of((from / size), size, Sort.by(ASC, "id"));

        Map<Long, Item> items = itemRepository.findAllItemsByOwnerId(id, pageRequest).getContent().stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        Map<Item, List<Comment>> comments = commentRepository.findByItemIn(new ArrayList<>(items.values()),
                        Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));

        Map<Item, List<Booking>> bookings = bookingRepository.findAllByItemOwnerId(id,
                        Sort.by(DESC, "start"))
                .stream()
                .collect(groupingBy(Booking::getItem));

        return new ArrayList<>(items.values()).stream()
                .map(item -> setItemComments(item, comments))
                .peek(i -> i.setLastBooking(getLastBookings(bookings.get(items.get(i.getId())))))
                .peek(i -> i.setNextBooking(getNextBookings(bookings.get(items.get(i.getId())))))
                .collect(toList());
    }

    private BookingNextLastDto getLastBookings(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart));
        return lastBooking.map(booking -> bookingMapper.toBookingLastNextDto(booking)).orElse(null);
    }

    private BookingNextLastDto getNextBookings(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        Optional<Booking> nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart));
        return nextBooking.map(booking -> bookingMapper.toBookingLastNextDto(booking)).orElse(null);
    }

    private Booking getNextBooking(Long itemId) {
        return bookingRepository.findTopByItemIdAndStatusNotAndStartAfterOrderByStartAsc(
                        itemId,
                        BookingStatus.REJECTED,
                        LocalDateTime.now())
                .orElse(null);
    }

    private ItemWithBookingsDateDto setItemComments(Item item, Map<Item, List<Comment>> comments) {
        if (comments.isEmpty() || comments.get(item) == null) {
            return ItemMapper.toItemResponseDtoWithBookings(item, CommentMapper.toCommentDto(new ArrayList<>()));
        }
        return ItemMapper.toItemResponseDtoWithBookings(item, CommentMapper.toCommentDto(comments.get(item)));

    }

    private Booking getLatestBooking(Long itemId) {
        return bookingRepository.findTopByItemIdAndStartBeforeOrderByStartDesc(itemId, LocalDateTime.now())
                .orElse(null);
    }






    public List<ItemDto> searchItems(String text, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new BadRequestException("Неверные параметры пагинации");
        }

        Pageable pageable = CustomPageRequest.customOf(from, size);

        return itemMapper.toItemDto(itemRepository.search(text, pageable).getContent());

    }

    public ItemDto createItem(Long id, ItemDto itemDto) {
        User owner = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));

            itemDto.setRequest(itemRequest);

        }

        Item item = itemMapper.toItem(owner, itemDto);

        itemRepository.save(item);

        return itemMapper.toItemDto(item);
    }

    public ItemDto updateItem(Long idItem, Long idOwner, ItemDto itemDto) {
        userRepository
                .findById(idOwner)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository
                .findItemByIdAndOwnerId(idItem, idOwner)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена или ошибка доступа"));

        item.setAvailable(
                itemDto.getAvailable() == null
                        ? item.getAvailable()
                        : itemDto.getAvailable()
        );
        item.setDescription(
                itemDto.getDescription() == null ||
                        itemDto.getDescription().isBlank()
                        ? item.getDescription()
                        : itemDto.getDescription());
        item.setName(
                itemDto.getName() == null ||
                        itemDto.getName().isBlank()
                        ? item.getName()
                        : itemDto.getName()
        );


        return itemMapper.toItemDto(item);
    }

    public CommentDto createComment(Long userId, Long idItem, CommentDto commentDto) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository
                .findById(idItem)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена или ошибка доступа"));

        if (!bookingRepository.existsByBooker_IdAndEndIsBefore(userId, LocalDateTime.now())) {
            throw new BadRequestException("Нельзя оставлять комментарии если не пользовались вещью");
        }
        commentDto.setAuthor(user);
        commentDto.setItem(item);

        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto)));

    }
}