package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public RequestDto createRequest(RequestDto requestDto, long userId) {
        validForExistUser(userId);
        requestDto.setUserId(userId);
        requestDto.setCreated(LocalDateTime.now());
        return RequestMapper.requestToDto(requestRepository.save(RequestMapper.toRequest(requestDto)));
    }

    @Override
    public List<RequestDto> getRequests(long userId) {
        validForExistUser(userId);
        List<RequestDto> requestsForUser = requestRepository.findForUser(userId).stream()
                .map(request -> {
                    RequestDto requestDto = RequestMapper.requestToDto(request);
                    List<Item> items = itemRepository.findByRequestId(requestDto.getId());
                    requestDto.setItems(items);
                    return requestDto;
                }).collect(Collectors.toList());
        return requestsForUser;
    }

    @Override
    public List<RequestDto> getCurrentCountOfRequests(int from, int size, long userId) {
        List<RequestDto> requestsForUser = requestRepository.findAllExceptOwner(PageRequest.of(from, size), userId).stream()
                .map(request -> {
                    RequestDto requestDto = RequestMapper.requestToDto(request);
                    List<Item> items = itemRepository.findByRequestId(requestDto.getId());
                    requestDto.setItems(items);
                    return requestDto;
                }).collect(Collectors.toList());
        return requestsForUser;
    }

    @Override
    public RequestDto getRequestById(long requestId, long userId) {
        validForExistUser(userId);
        Optional<Request> currentRequestOpt = requestRepository.findById(requestId);
        if (currentRequestOpt.isPresent()) {
            RequestDto requestDto = RequestMapper.requestToDto(currentRequestOpt.get());
            if (!itemRepository.findByRequestId(requestDto.getId()).isEmpty()) {
                List<Item> items = itemRepository.findByRequestId(requestDto.getId());
                requestDto.setItems(items);
            } else {
                requestDto.setItems(new ArrayList<>());
            }
            return requestDto;
        }
        throw new NotFoundException("Не найдено");
    }

    private void validForExistUser(long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует");
        }
    }
}