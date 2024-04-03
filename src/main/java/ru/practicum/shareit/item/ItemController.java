package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.ResponseComment;
import ru.practicum.shareit.data.Constants;
import ru.practicum.shareit.item.dto.Check;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.comment.dto.RequestComment;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(Constants.TITLE_ITEM_BOOKING) Long userId, @RequestBody @Validated(Check.OnCreate.class) CreateItemDto createItemDto) {
        log.info("Получен запрос на добавление вещи");
        return itemService.createItem(userId, createItemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(Constants.TITLE_ITEM_BOOKING) Long userId, @RequestBody @Validated(Check.class) CreateItemDto itemDto,
                              @PathVariable("itemId") Long itemId) {
        log.info("Получен запрос на обновление информации о вещи");
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemForOwner(@RequestHeader(Constants.TITLE_ITEM_BOOKING) Long userId,
                                   @RequestParam(value = "from", defaultValue = "0") int from,
                                   @RequestParam(value = "size", defaultValue = "100") int size) {
        log.info("Владелец запросил список своих вещей");
        return itemService.getAllItemForOwner(from, size, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(Constants.TITLE_ITEM_BOOKING) Long userId,
                                    @RequestParam(name = "text", defaultValue = "") String text,
                                    @RequestParam(value = "from", defaultValue = "0") int from,
                                    @RequestParam(value = "size", defaultValue = "100") int size) {
        log.info("Запрос на поиск вещи по содержанию");
        return itemService.getItemForBooker(text, userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Long itemId,
                               @RequestHeader(value = Constants.TITLE_ITEM_BOOKING) long userId) {
        log.info("Получен запрос на поиск вещи по id");
        return itemService.getItemById(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseComment createComment(@RequestHeader(Constants.TITLE_ITEM_BOOKING) long idUser,
                                         @Valid @RequestBody RequestComment commentDto,
                                         @PathVariable("itemId") long itemId) {
        log.info("Получен запрос на добавление комментария");
        return itemService.createComment(idUser, commentDto, itemId);
    }
}