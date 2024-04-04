package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.RequestServiceImpl;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final RequestServiceImpl requestService;
    UserDto userDto1 = new UserDto();
    UserDto userDto2 = new UserDto();
    CreateItemDto itemDto1 = new CreateItemDto();
    ItemDto itemDto2 = new ItemDto();
    RequestDto itemRequestDto = new RequestDto();

    @BeforeEach
    void setUp() {
        userDto1.setName("testName1");
        userDto1.setEmail("test@mail.ru");
        userDto2.setName("update");
        userDto2.setEmail("update@mail.ru");
        itemDto1.setAvailable(true);
        itemDto1.setDescription("test");
        itemDto1.setName("test");
        itemDto1.setRequestId(1);
        itemDto2.setAvailable(true);
        itemDto2.setDescription("update");
        itemDto2.setName("update");
        itemRequestDto.setDescription("test");
    }

    @Test
    void createItemRequest() {
        UserDto userDtoCreate = userService.createUser(userDto1);
        RequestDto itemRequestCreate = requestService.createRequest(itemRequestDto, userDtoCreate.getId());
        TypedQuery<Request> createItemRequest =
                em.createQuery("SELECT r FROM Request r WHERE r.id = :id", Request.class);
        Request item = createItemRequest.setParameter("id", itemRequestCreate.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(itemRequestCreate.getId()));
        assertThat(item.getDescription(), equalTo(itemRequestCreate.getDescription()));
        assertThat(item.getName(), equalTo(itemRequestCreate.getName()));
    }

    @Test
    void getCurrentCountOfRequest() {

        UserDto userDtoCreate = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        RequestDto itemRequestCreate =
                requestService.createRequest(itemRequestDto, userDtoCreate.getId());
        RequestDto getRequest = requestService.getRequestById(1, 1);

        assertThat(1, equalTo(requestService.getCurrentCountOfRequests(0, 1, 2).size()));
    }

    @Test
    void createItemRequestWrongIdUser() {
        UserDto userDtoCreate = userService.createUser(userDto1);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> requestService.createRequest(itemRequestDto, userDtoCreate.getId() + 1));
        assertThat(notFoundException.getMessage(),
                equalTo("Такого пользователя не существует"));
    }

    @Test
    void findItemRequestById() {
        UserDto userDtoCreate = userService.createUser(userDto1);
        RequestDto itemRequestCreate = requestService.createRequest(itemRequestDto, userDtoCreate.getId());
        TypedQuery<Request> createItemRequest =
                em.createQuery("SELECT r FROM Request r WHERE r.id = :id", Request.class);
        Request item = createItemRequest.setParameter("id", itemRequestCreate.getId()).getSingleResult();
        RequestDto itemRequestDto =
                requestService.getRequestById(userDtoCreate.getId(), itemRequestCreate.getId());

        assertThat(item.getId(), equalTo(itemRequestDto.getId()));
        assertThat(item.getName(), equalTo(itemRequestDto.getName()));
    }

    @Test
    void findItemRequestByIdWrongIdUser() {
        UserDto userDtoCreate = userService.createUser(userDto1);
        RequestDto requsetDto = requestService.createRequest(itemRequestDto, userDtoCreate.getId());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> requestService
                        .getRequestById(userDtoCreate.getId(), requsetDto.getUserId() + 10));
        assertThat(notFoundException.getMessage(),
                equalTo("Такого пользователя не существует"));
    }

    @Test
    void findItemRequestByIdWrongIdItemRequest() {
        UserDto userDto = userService.createUser(userDto1);
        RequestDto itemRequestCreate = requestService.createRequest(itemRequestDto, userDto.getId());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> requestService
                        .getRequestById(itemRequestCreate.getId() + 10, userDto.getId()));
        assertThat(notFoundException.getMessage(),
                equalTo("Не найдено"));
    }

    @Test
    void findAllItemRequest() {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        RequestDto itemRequestCreate =
                requestService.createRequest(itemRequestDto, userDtoCreate1.getId());
        TypedQuery<Request> createItemRequest =
                em.createQuery("SELECT r FROM Request r WHERE r.id = :id", Request.class);
        List<Request> items = createItemRequest.setParameter("id", itemRequestCreate.getId()).getResultList();
        List<RequestDto> itemRequestDtos =
                requestService.getRequests(userDtoCreate1.getId());

        assertThat(items.size(), equalTo(itemRequestDtos.size()));
        assertThat(items.get(0).getId(), equalTo(itemRequestDtos.get(0).getId()));
    }

    @Test
    void findAllItemRequestWrongIdUser() {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        RequestDto itemRequestCreate =
                requestService.createRequest(itemRequestDto, userDtoCreate1.getId());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> requestService
                        .getRequestById(userDtoCreate1.getId(), -1));

        assertThat(notFoundException.getMessage(),
                equalTo("Такого пользователя не существует"));
    }

    @Test
    void findAllItemRequestWithItem() {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        RequestDto itemRequestCreate =
                requestService.createRequest(itemRequestDto, userDtoCreate1.getId());
        TypedQuery<Request> createItemRequest =
                em.createQuery("SELECT r FROM Request r WHERE r.id = :id", Request.class);
        List<Request> items = createItemRequest.setParameter("id", itemRequestCreate.getId()).getResultList();
        List<RequestDto> itemRequestDtos =
                requestService.getRequests(userDtoCreate1.getId());

        assertThat(items.size(), equalTo(itemRequestDtos.size()));
        assertThat(items.get(0).getId(), equalTo(itemRequestDtos.get(0).getId()));
    }

    @Test
    void findAllItemRequestForRequestorWithItem() {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        RequestDto itemRequestCreate =
                requestService.createRequest(itemRequestDto, userDtoCreate1.getId());
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        TypedQuery<Request> createItemRequest =
                em.createQuery("SELECT r FROM Request r WHERE r.id = :id", Request.class);
        List<Request> items = createItemRequest.setParameter("id", itemRequestCreate.getId()).getResultList();

        List<RequestDto> itemRequestDtos = requestService.getRequests(userDtoCreate1.getId());

        assertThat(items.size(), equalTo(itemRequestDtos.size()));
        assertThat(items.get(0).getId(), equalTo(itemRequestDtos.get(0).getId()));
    }

    @Test
    void findAllItemRequestForRequestorWithItemWrongIdUser() {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        RequestDto itemRequestCreate =
                requestService.createRequest(itemRequestDto, userDtoCreate1.getId());
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate2.getId(), itemDto1);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> requestService.getRequests(userDtoCreate1.getId() + 2));

        assertThat(notFoundException.getMessage(),
                equalTo("Такого пользователя не существует"));
    }


}