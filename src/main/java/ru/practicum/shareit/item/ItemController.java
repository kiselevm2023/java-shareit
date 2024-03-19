package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    private static final String TITLE_ITEM = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemServiceImpl itemServiceImpl) {
        this.itemService = itemServiceImpl;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(TITLE_ITEM) Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи");
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(TITLE_ITEM) Long userId, @RequestBody ItemDto itemDto,
                              @PathVariable("itemId") Long itemId) {
        log.info("Получен запрос на обновление информации о вещи");
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemForOwner(@RequestHeader(TITLE_ITEM) Long userId) {
        log.info("Владелец запросил список своих вещей");
        return itemService.getAllItemForOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(TITLE_ITEM) Long userId,
                                    @RequestParam(name = "text", defaultValue = "") String text) {
        log.info("Запрос на поиск вещи по содержанию");
        return itemService.getItemForBooker(text, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Long itemId,
                               @RequestHeader(value = TITLE_ITEM) long userId) {
        log.info("Получен запрос на поиск вещи по id");
        return itemService.getItemById(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public Comment createComment(@RequestHeader(TITLE_ITEM) long idUser,
                                 @RequestBody CommentDto commentDto,
                                 @PathVariable("itemId") long itemId) {
        log.info("Получен запрос на добавление комментария");
        return itemService.createComment(idUser, commentDto, itemId);
    }
}