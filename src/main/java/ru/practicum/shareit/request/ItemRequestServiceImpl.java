package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestMapper itemRequestMapper;

    @Transactional
    public ItemRequestDto createItemRequestDto(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.searchByIdOrThrow(userId);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(user, itemRequestDto);
        ItemRequest savedItemRequest = requestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    public List<ItemRequestDto> getItemsRequests(Long userId) {

        userRepository.searchByIdOrThrow(userId);

        List<ItemRequest> itemRequests = requestRepository.getAllItemRequestsByOwnerId(userId);
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests) {
            List<ItemDto> itemsDtos = itemMapper
                    .toItemDto(itemRepository.findAllItemsByRequestId(itemRequest.getId()));

            ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDtoWithItems(itemRequest, itemsDtos);

            itemRequestDtos.add(itemRequestDto);

        }
        return itemRequestDtos;
    }


    public ItemRequestDto getItemRequests(Long userId, Long requestId) {

        userRepository.searchByIdOrThrow(userId);

        ItemRequest itemRequest = requestRepository.searchByIdOrThrow(requestId);

        List<ItemDto> itemsDtos = itemMapper
                .toItemDto(itemRepository.findAllItemsByRequestId(requestId));

        return itemRequestMapper.toItemRequestDto(itemRequest, itemsDtos);
    }

    public List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new BadRequestException("Неверные параметры пагинации");
        }

        userRepository.searchByIdOrThrow(userId);

        Pageable sortedByCreatedDesc =
                PageRequest.of(((int) Math.floor((double) from / size)),
                        size, Sort.by("created").descending());

        List<ItemRequest> itemRequests = requestRepository
                .findAllRequestsByRequestorIdNot(userId, sortedByCreatedDesc).getContent();

        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests) {
            List<ItemDto> itemsDtos = itemMapper
                    .toItemDto(itemRepository.findAllItemsByRequestId(itemRequest.getId()));

            ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDtoWithItems(itemRequest, itemsDtos);

            itemRequestDtos.add(itemRequestDto);

        }
        return itemRequestDtos;

    }
}