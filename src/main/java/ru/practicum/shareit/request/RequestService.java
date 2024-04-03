package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(RequestDto requestDto, long userId);

    List<RequestDto> getRequests(long userId);

    List<RequestDto> getCurrentCountOfRequests(int from, int size, long userId);

    RequestDto getRequestById(long requestId, long userId);
}