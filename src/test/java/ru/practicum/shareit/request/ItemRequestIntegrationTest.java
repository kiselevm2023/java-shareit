package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;


import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.user.User;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ItemRequestIntegrationTest {
    @Autowired
    private UserRepository userStorage;
    @Autowired
    private ItemServiceImpl itemService;
    @Autowired
    private RequestServiceImpl requestService;
    private User requester;
    private User owner;

    @BeforeEach
    void setup() {
        requester = new User();
        requester.setName("requester");
        requester.setEmail("requester@user.com");

        owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@user.com");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testCreateItemRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("need item");

        assertThrows(NotFoundException.class, () -> requestService.createRequest(requestDto, 1L),
                "Пользователей пока не зарегистрировано");

        requester = userStorage.save(requester);
        RequestDto responseDto = requestService.createRequest(requestDto, requester.getId());

        assertEquals(responseDto.getDescription(), requestService.getRequestById(requester.getId(), responseDto.getId()).getDescription());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testFindAllByAuthorId() {
        assertThrows(NotFoundException.class, () -> requestService.getRequests(1L));

        requester = userStorage.save(requester);
        owner = userStorage.save(owner);

        assertEquals(0, requestService.getRequests(requester.getId()).size());

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("need item1");

        RequestDto request = requestService.createRequest(requestDto, requester.getId());

        requestDto.setDescription("need item2");

        requestService.createRequest(requestDto, requester.getId());

        requestDto.setDescription("owner request");

        assertEquals(2, requestService.getRequests(requester.getId()).size());
        assertEquals(0, requestService.getRequests(owner.getId()).size());

        CreateItemDto itemRequestDto = new CreateItemDto();
        itemRequestDto.setName("item");
        itemRequestDto.setDescription("desc");
        itemRequestDto.setAvailable(true);
        itemRequestDto.setRequestId(request.getId());

        itemService.createItem(requester.getId(), itemRequestDto);

        assertEquals(0, requestService.getRequests(requester.getId()).get(0).getItems().size());
        assertEquals(itemRequestDto.getName(),
                requestService.getRequests(requester.getId()).get(1).getItems().get(0).getName());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testFindAllRequests() {
        assertThrows(NotFoundException.class, () -> requestService.getCurrentCountOfRequests(0, 20, 1L));

        requester = userStorage.save(requester);
        owner = userStorage.save(owner);

        assertEquals(0, requestService.getCurrentCountOfRequests(0, 20, requester.getId()).size());

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("need item1");
        RequestDto request = requestService.createRequest(requestDto, requester.getId());
        requestDto.setDescription("need item2");
        requestService.createRequest(requestDto, requester.getId());
        requestDto.setDescription("owner request");

        assertEquals(0, requestService.getCurrentCountOfRequests(0, 20, owner.getId()).get(0).getItems().size());
        assertEquals(2, requestService.getCurrentCountOfRequests(0, 20, owner.getId()).size());
        assertEquals(0, requestService.getCurrentCountOfRequests(0, 20, requester.getId()).size());

        CreateItemDto itemRequestDto = new CreateItemDto();
        itemRequestDto.setName("item");
        itemRequestDto.setDescription("desc");
        itemRequestDto.setAvailable(true);
        itemRequestDto.setRequestId(request.getId());
        itemService.createItem(owner.getId(), itemRequestDto);

        assertEquals(1, requestService.getCurrentCountOfRequests(0, 20, owner.getId())
                .get(1).getItems().size());
        assertEquals(0, requestService.getCurrentCountOfRequests(0, 20, owner.getId())
                .get(0).getItems().size());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testFindById() {
        assertThrows(NotFoundException.class, () -> requestService.getRequestById(1L, 1L));

        requester = userStorage.save(requester);

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(1L, 1L));

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("need item1");
        RequestDto request = requestService.createRequest(requestDto, requester.getId());

        assertEquals(request.getDescription(), requestService.getRequestById(requester.getId(), request.getId()).getDescription());
        assertEquals(0, requestService.getRequestById(requester.getId(), request.getId()).getItems().size());

        owner = userStorage.save(owner);
        CreateItemDto itemRequestDto = new CreateItemDto();
        itemRequestDto.setName("item");
        itemRequestDto.setDescription("desc");
        itemRequestDto.setAvailable(true);
        itemRequestDto.setRequestId(request.getId());
        itemService.createItem(owner.getId(), itemRequestDto);

        assertEquals(1, requestService.getRequestById(requester.getId(), request.getId()).getItems().size());
    }
}
