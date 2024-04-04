package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;

import java.util.ArrayList;
import java.util.List;

public class RequestMapper {

    public static RequestDto requestToDto(Request request) {

        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setName(request.getName());
        requestDto.setDescription(request.getDescription());
        requestDto.setCreated(request.getCreated());
        requestDto.setItemId(request.getItemId());
        requestDto.setAvailable(request.isAvailable());
        requestDto.setUserId(request.getUserId());
        return requestDto;
    }

    public static Request toRequest(RequestDto requestDto) {

        Request request = new Request();
        if (requestDto.getId() != 0) {
            request.setId(requestDto.getId());
        }
        request.setName(requestDto.getName());
        request.setDescription(requestDto.getDescription());
        request.setCreated(requestDto.getCreated());
        request.setItemId(requestDto.getItemId());
        request.setAvailable(requestDto.isAvailable());
        request.setUserId(requestDto.getUserId());
        return request;
    }
}