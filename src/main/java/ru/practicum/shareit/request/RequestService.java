package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(ItemRequestDto requestDto, long userId);

    List<RequestDto> getRequests(long userId);

    List<RequestDto> getCurrentCountOfRequests(int from, int size, long userId);

    RequestDto getRequestById(long requestId, long userId);
}