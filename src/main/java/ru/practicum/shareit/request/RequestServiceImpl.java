package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.item.model.ItemMapper;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService   {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public RequestDto createRequest(ItemRequestDto requestDto, long userId) {
        log.info("ItemRequestService: обработка запроса от пользователя {} на добавление ItemRequest {}",
                userId, requestDto.toString());
        User author = userRepository.searchByIdOrThrow(userId);

        Request itemRequest = RequestMapper.toRequest(requestDto, author);
        return RequestMapper.requestToDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<RequestDto> getRequests(long userId) {
        log.info("ItemRequestService: обработка запроса от пользователя {} на поиск всех его ItemRequest", userId);
        User user = userRepository.searchByIdOrThrow(userId);
        List<Request> authorsRequests = requestRepository.findAllByAuthorId(userId, Sort.by(DESC, "created"));
        Map<Request, List<Item>> requestMap = itemRepository.findByItemRequestIn(authorsRequests, Sort.by(ASC, "id"))
                .stream()
                .collect(groupingBy(Item::getItemRequest, toList()));
        return authorsRequests.stream()
                .map(itemRequest -> setItemRequestItems(itemRequest, requestMap.get(itemRequest)))
                .collect(toList());
    }

    @Override
    public List<RequestDto> getCurrentCountOfRequests(int from, int size, long userId) {
        log.info("ItemRequestService: обработка запроса от пользователя {} на поиск всех ItemRequest", userId);

        User user = userRepository.searchByIdOrThrow(userId);

        PageRequest pageRequest = PageRequest.of((from / size), size, Sort.by(DESC, "created"));
        List<Request> responseDtoList = requestRepository.findAllByAuthorIdNot(userId, pageRequest).getContent();
        Map<Request, List<Item>> itemsMap = itemRepository.findByItemRequestIn(responseDtoList, Sort.by(ASC, "id"))
                .stream()
                .collect(groupingBy(Item::getItemRequest, toList()));
        return responseDtoList.stream()
                .map(itemRequest -> setItemRequestItems(itemRequest, itemsMap.get(itemRequest)))
                .collect(toList());
    }

    @Override
    public RequestDto getRequestById(long requestId, long userId) {
        log.info("ItemRequestService: обработка запроса от пользователя {} на поиск ItemRequest с id {}",
                userId, requestId);
        User user = userRepository.searchByIdOrThrow(userId);
        List<Item> itemList = itemRepository.findByItemRequestId(requestId, Sort.by(ASC, "id"));
        Request itemRequest = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(
                String.format("ItemRequest с id %d не найден", requestId)
        ));
        return setItemRequestItems(itemRequest, itemList);
    }

    private RequestDto setItemRequestItems(Request itemRequest, List<Item> items) {
        log.info("ItemRequestService: конвертация ItemRequest c id {} в Dto и добавление вещей, добавленных по запросу",
                itemRequest.getId());
        List<ItemDto> itemResponseDtoList = new ArrayList<>();
        if (items != null) {
            for (Item item : items) {
                itemResponseDtoList.add(ItemMapper.toItemDto(item));
            }
        }
        RequestDto responseDto = RequestMapper.requestToDto(itemRequest);
        responseDto.setItems(itemResponseDtoList);
        return responseDto;
    }

    private void checkUser(Long userId) {
        log.info("ItemRequestService: проверка регистрации пользователя {}", userId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id %d не найден", userId)));
    }
}