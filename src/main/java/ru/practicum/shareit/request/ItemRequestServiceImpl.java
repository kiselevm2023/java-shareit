package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestMapper itemRequestMapper;

    public ItemRequestDto createItemRequestDto(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.searchByIdOrThrow(userId);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(user, itemRequestDto);
        ItemRequest savedItemRequest = requestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    /*public List<ItemRequestDto> getItemsRequests(Long userId) {

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
    }  */

    @Override
    public List<ItemRequestDto> getItemsRequests(Long userId) {
        userRepository.searchByIdOrThrow(userId);
        List<ItemRequest> authorsRequests = requestRepository.findAllByRequestorId(userId, Sort.by(DESC, "created"));
        Map<ItemRequest, List<Item>> requestMap = itemRepository.findByRequestIn(authorsRequests, Sort.by(ASC, "id"))
                .stream()
                .collect(groupingBy(Item::getRequest, toList()));
        return authorsRequests.stream()
                .map(itemRequest -> setItemRequestItems(itemRequest, requestMap.get(itemRequest)))
                .collect(toList());
    }

    public ItemRequestDto getItemRequests(Long userId, Long requestId) {

        userRepository.searchByIdOrThrow(userId);

        ItemRequest itemRequest = requestRepository.searchByIdOrThrow(requestId);

        List<ItemDto> itemsDtos = itemMapper
                .toItemDto(itemRepository.findAllItemsByRequestId(requestId));

        return itemRequestMapper.toItemRequestDto(itemRequest, itemsDtos);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size) {

        if (size <= 0 || from < 0) {
            throw new BadRequestException("Неверные параметры пагинации");
        }
        userRepository.searchByIdOrThrow(userId);
        PageRequest pageRequest = PageRequest.of((from / size), size, Sort.by(DESC, "created"));
        List<ItemRequest> responseDtoList = requestRepository.findAllRequestsByRequestorIdNot(userId, pageRequest).getContent();
        Map<ItemRequest, List<Item>> itemsMap = itemRepository.findByRequestIn(responseDtoList, Sort.by(ASC, "id"))
                .stream()
                .collect(groupingBy(Item::getRequest, toList()));
        return responseDtoList.stream()
                .map(itemRequest -> setItemRequestItems(itemRequest, itemsMap.get(itemRequest)))
                .collect(toList());
    }

    private ItemRequestDto setItemRequestItems(ItemRequest itemRequest, List<Item> items) {

        List<ItemDto> itemResponseDtoList = new ArrayList<>();
        if (items != null) {
            for (Item item : items) {
                itemResponseDtoList.add(itemMapper.toItemDto(item));
            }
        }
        ItemRequestDto responseDto = itemRequestMapper.toItemRequestDto(itemRequest);
        responseDto.setItems(itemResponseDtoList);
        return responseDto;
    }
}