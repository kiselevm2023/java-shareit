package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.User;

@UtilityClass
public class RequestMapper {
    public Request toRequest(ItemRequestDto itemRequestRequestDto, User author) {
        if (itemRequestRequestDto == null)
            return null;
        Request itemRequest = new Request();
        itemRequest.setAuthor(author);
        itemRequest.setDescription(itemRequestRequestDto.getDescription());
        itemRequest.setCreated(itemRequestRequestDto.getCreated());
        return itemRequest;
    }

    public RequestDto requestToDto(Request itemRequest) {
        if (itemRequest == null)
            return null;
        RequestDto responseDto = new RequestDto();
        responseDto.setId(itemRequest.getId());
        responseDto.setDescription(itemRequest.getDescription());
        responseDto.setCreated(itemRequest.getCreated());
        return responseDto;
    }
}