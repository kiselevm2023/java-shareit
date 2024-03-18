package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
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
        log.info("Request is received to add an item");
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(TITLE_ITEM) Long userId, @RequestBody ItemDto itemDto,
                              @PathVariable("itemId") Long itemId) {
        log.info("Request is received to update information about an item");
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemForOwner(@RequestHeader(TITLE_ITEM) Long userId) {
        log.info("The owner requested a list of his things");
        return itemService.getAllItemForOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text", defaultValue = "") String text) {
        log.info("Request to search for an item by content");
        return itemService.searchItem(text);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Long itemId) {
        log.info("Request is received to search for an item by id");
        return itemService.getItemById(itemId);
    }
}