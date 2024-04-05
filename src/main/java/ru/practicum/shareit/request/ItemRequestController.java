package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final RequestService requestService;

    @Autowired
    public ItemRequestController(RequestServiceImpl requestServiceImpl) {
        this.requestService = requestServiceImpl;
    }

    @PostMapping
    public RequestDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("Получен запрос на создание запроса о бронировании");
        return requestService.createRequest(requestDto, userId);
    }

    @GetMapping
    public List<RequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получение всех запросов без ограничений");
        return requestService.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable("requestId") long requestId) {
        log.info("Получение определенного запроса");
        return requestService.getRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getCurrentCountOfRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(value = "from", defaultValue = "0") int from,
                                                      @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Получение определенного количества запросов");
        return requestService.getCurrentCountOfRequests(from, size, userId);
    }

}