package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.RequestComment;
import ru.practicum.shareit.comment.dto.ResponseComment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import static ru.practicum.shareit.item.model.ItemMapper.mapToItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(Long userId, CreateItemDto createItemDto) {
        Optional<User> userOpt = userRepository.findByIdOrThrow(userId);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.createToItem(createItemDto, userOpt.get())));
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        Optional<User> userOptional = userRepository.findByIdOrThrow(userId);
        Optional<Item> itemOptional = itemRepository.findByIdOrThrow(itemId);
        validateForOwner(userId, itemOptional.get().getOwner().getId(), itemId);
        Item oldItem = itemOptional.get();
        String newName = itemDto.getName();
        if (newName != null && !newName.isBlank()) {
            oldItem.setName(newName);
        }
        String newDescription = itemDto.getDescription();
        if (newDescription != null && !newDescription.isBlank()) {
            oldItem.setDescription(newDescription);
        }
        Boolean newAvailable = itemDto.getAvailable();
        if (newAvailable != null) {
            oldItem.setAvailable(newAvailable);
        }
        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    public ItemDto getItemById(long itemId, long ownerId) {
        User user = userRepository.searchByIdOrThrow(ownerId);
        Item item = itemRepository.searchByIdOrThrow(itemId);
        return getItemDtoWithBookingAndComments(item, ownerId);
    }

    @Override
    public List<ItemDto> getItemForBooker(String text, long userId) {
        userRepository.findByIdOrThrow(userId);
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.getItemForBooker(text.toLowerCase().trim())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItemForOwner(long userId) {

        userRepository.findByIdOrThrow(userId);

        Map<Long, Item> items = itemRepository.findAllByOwnerId(userId, Sort.by(ASC, "id")).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        Map<Item, List<Comment>> comments = commentRepository.findByItemIn(new ArrayList<>(items.values()),
                        Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));

        Map<Item, List<Booking>> bookings = bookingRepository.findAllByItemOwnerId(userId,
                        Sort.by(DESC, "start"))
                .stream()
                .collect(groupingBy(Booking::getItem));
        LocalDateTime currentTime = LocalDateTime.now();
        return new ArrayList<>(items.values()).stream()
                .map(item -> setItemComments(item, comments))
                .peek(i -> i.setLastBooking(getLastBooking(bookings.get(items.get(i.getId())), currentTime)))
                .peek(i -> i.setNextBooking(getNextBooking(bookings.get(items.get(i.getId())), currentTime)))
                .collect(toList());
    }

    @Override
    public ResponseComment createComment(long userId, RequestComment commentDto, long itemId) {
        validateComment(commentDto);
        Optional<User> userOptional = userRepository.findByIdOrThrow(userId);
        Optional<Item> itemOptional = itemRepository.findByIdOrThrow(itemId);
        List<Booking> bookings = bookingRepository.findBookingByItem(itemId, userId, Sort.by(Sort.Direction.DESC, "start"));
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь не бронировал эту вещь");
        }
        Comment comment = CommentMapper.requestToComment(commentDto, itemOptional.get(), userOptional.get().getName());
        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    private ItemDto getItemDtoWithBookingAndComments(Item item, long ownerId) {
        List<ResponseComment> commentsDto = commentRepository.findAllByItemIdOrderByIdDesc(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (item.getOwner().getId() != ownerId) {
            return mapToItemDto(item, null, null, commentsDto);
        }
        LocalDateTime currentTime = LocalDateTime.now();
        BookingItemDto lastBooking = bookingService.getLastItemBooking(item.getId(), ownerId, currentTime);
        BookingItemDto nextBooking = bookingService.getNextItemBooking(item.getId(), ownerId, currentTime);
        return mapToItemDto(item, lastBooking, nextBooking, commentsDto == null ? List.of() : commentsDto);
    }

    private void validateFoundForItem(Optional<Item> itemOptional) {
        if (itemOptional.isEmpty()) {
            throw new NotFoundException("Вещь с данным id не найдена");
        }
    }

    private void validateUserFounded(Optional<User> user) {
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validateForOwner(long userId, long ownerId, long itemId) {
        if (ownerId != userId) {
            throw new NotFoundException("У пользователя с id = " + userId + " нет вещи с id = " + itemId);
        }
    }

    private void validateComment(RequestComment commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new ValidationException("Пустой комментарий");
        }
    }

    private ItemOwnerDto setItemComments(Item item, Map<Item, List<Comment>> comments) {
        if (comments.isEmpty() || comments.get(item) == null) {
            return ItemMapper.toItemResponseDtoWithBookings(item);
        }
        return ItemMapper.toItemResponseDtoWithBookings(item, CommentMapper.toCommentResponseDtoList(comments.get(item)));
    }

    private BookingItemDto getLastBooking(List<Booking> bookings, LocalDateTime currentTime) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(currentTime)  || booking.getStart().isEqual(currentTime))
                .max(Comparator.comparing(Booking::getStart));
        return lastBooking.map(BookingMapper::toBookingResponseDto).orElse(null);
    }

    private BookingItemDto getNextBooking(List<Booking> bookings, LocalDateTime currentTime) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        Optional<Booking> nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(currentTime))
                .min(Comparator.comparing(Booking::getStart));
        return nextBooking.map(BookingMapper::toBookingResponseDto).orElse(null);
    }
}