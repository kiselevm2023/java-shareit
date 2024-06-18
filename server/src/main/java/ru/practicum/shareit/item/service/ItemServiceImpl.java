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
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.request.ItemRequest;

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
    private final UserMapper userMapper;
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
                .map(comment -> CommentMapper.toCommentDto(comment, userMapper, itemMapper))
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
        if (size <= 0 || from < 0) {
            throw new BadRequestException("Неверные параметры пагинации");
        }

        User user = userRepository.searchByIdOrThrow(id);

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
        LocalDateTime currentTime = LocalDateTime.now();
        return new ArrayList<>(items.values()).stream()
                .map(item -> setItemComments(item, comments))
                .peek(i -> i.setLastBooking(getLastBookings(bookings.get(items.get(i.getId())), currentTime)))
                .peek(i -> i.setNextBooking(getNextBookings(bookings.get(items.get(i.getId())), currentTime)))
                .collect(toList());
    }

    private BookingNextLastDto getLastBookings(List<Booking> bookings, LocalDateTime currentTime) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> !booking.getStart().isAfter(currentTime))
                .max(Comparator.comparing(Booking::getStart));
        return lastBooking.map(booking -> bookingMapper.toBookingLastNextDto(booking)).orElse(null);
    }

    private BookingNextLastDto getNextBookings(List<Booking> bookings, LocalDateTime currentTime) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        Optional<Booking> nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(currentTime))
                .min(Comparator.comparing(Booking::getStart));
        return nextBooking.map(booking -> bookingMapper.toBookingLastNextDto(booking)).orElse(null);
    }

    private ItemWithBookingsDateDto setItemComments(Item item, Map<Item, List<Comment>> comments) {
        if (comments.isEmpty() || comments.get(item) == null) {
            return ItemMapper.toItemResponseDtoWithBookings(item, CommentMapper.toCommentDtoList(new ArrayList<>(), userMapper, itemMapper));
        }
        return ItemMapper.toItemResponseDtoWithBookings(item, CommentMapper.toCommentDtoList(comments.get(item), userMapper, itemMapper));

    }

    public List<ItemDto> searchItems(String text, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new BadRequestException("Неверные параметры пагинации");
        }

        Pageable pageable = CustomPageRequest.customOf(from, size);

        return itemMapper.toItemDto(itemRepository.search(text, pageable).getContent());

    }

    @Override
    public ItemDto createItem(Long id, ItemDto itemDto) {

        User owner = userRepository.searchByIdOrThrow(id);

        Item item = itemMapper.toItem(owner, itemDto);

        if (itemDto.getRequestId() != null) {
            ItemRequest request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("ItemRequest с id " + itemDto.getRequestId() + " не найден"));
            item.setRequest(request);
        }

        Item savedItem = itemRepository.save(item);
        return itemMapper.toItemDto(savedItem);
    }

    public ItemDto updateItem(Long idItem, Long idOwner, ItemDto itemDto) {

        User user = userRepository.searchByIdOrThrow(idOwner);

        Item item = itemRepository.findItemByIdAndOwnerIdOrThrow(idItem, idOwner);

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

        User user = userRepository.searchByIdOrThrow(userId);

        Item item = itemRepository.searchByIdOrThrow(idItem);

        if (!bookingRepository.existsByBooker_IdAndEndIsBefore(userId, LocalDateTime.now())) {
            throw new BadRequestException("Нельзя оставлять комментарии если не пользовались вещью");
        }
        commentDto.setAuthor(userMapper.toUserDto(user));
        commentDto.setItem(itemMapper.toItemDto(item));
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, user, item));

        return CommentMapper.toCommentDto(comment, userMapper, itemMapper);

    }
}