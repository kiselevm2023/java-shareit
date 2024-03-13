package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.Forbidden;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private Map<Long, Item> items = new HashMap<>();
    private Map<Long, List<Long>> itemsPerOwner = new HashMap<>();

    private long id = 1;

    @Override
    public Item createItem(Long userId, Item item) {
        item.setId(id);
        item.setOwner(userId);
        List<Long> itemList;
        if (itemsPerOwner.containsKey(userId)) {
            itemList = itemsPerOwner.get(userId);
        } else {
            itemList = new ArrayList<>();
        }
        itemList.add(item.getId());
        itemsPerOwner.put(userId, itemList);
        items.put(item.getId(), item);
        id++;
        return item;
    }

    @Override
    public Item updateItem(long userId, Item item, long itemId) {
        Item oldItem = items.get(itemId);
        if (!items.containsKey(itemId) || items.get(itemId).getOwner() != userId) {
            throw new Forbidden("Only the owner has the right to change the parameters of the item");
        }
        String newName = item.getName();
        if (newName != null && !newName.isBlank()) {
            oldItem.setName(newName);
        }
        String newDescription = item.getDescription();
        if (newDescription != null && !newDescription.isBlank()) {
            oldItem.setDescription(newDescription);
        }
        Boolean newAvailable = item.getAvailable();
        if (item.getAvailable() != null) {
            oldItem.setAvailable(newAvailable);
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllItemForOwner(Long userId) {
        List<Long> itemIdForOwner = itemsPerOwner.get(userId);
        List<Item> itemsForReturn = new ArrayList<>();
        for (Long id : itemIdForOwner) {
            itemsForReturn.add(items.get(id));
        }
        return itemsForReturn;
    }

    @Override
    public List<Item> searchItem(String text) {
        String editText = text.toLowerCase();
        List<Item> itemsSearched = new ArrayList<>();
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(editText)
                    || item.getDescription().toLowerCase().contains(editText))
                    && item.getAvailable()) {
                itemsSearched.add(item);
            }
        }
        return itemsSearched;
    }

    @Override
    public Item getItemById(Long itemId) {
        try {
            return items.get(itemId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Things don't exist");
        }
    }
}
