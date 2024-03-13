package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService usersRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        usersRepository.getUserById(userId);
        return ItemMapper.toItemDto(itemRepository.createItem(userId, ItemMapper.toItem(itemDto)));

    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        usersRepository.getUserById(userId);
        return ItemMapper.toItemDto(itemRepository.updateItem(userId, ItemMapper.toItem(itemDto), itemId));
    }

    @Override
    public List<ItemDto> getAllItemForOwner(Long userId) {
        usersRepository.getUserById(userId);
        return itemRepository.getAllItemForOwner(userId).stream()
                .map(x -> ItemMapper.toItemDto(x)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchItem(text).stream()
                .map(x -> ItemMapper.toItemDto(x)).collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }
}