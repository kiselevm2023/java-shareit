package ru.practicum.shareit.itemRequest;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {
    private final EntityManager em;
    private final UserController userController;
    private final ItemRequestController itemRequestController;

    UserDto userDto1 = new UserDto();
    UserDto userDto2 = new UserDto();
    RequestDto requestDto = new RequestDto();

    @BeforeEach
    void setUp() {
        userDto1.setName("test");
        userDto1.setEmail("test@mail.ru");
        userDto2.setName("test2");
        requestDto.setDescription("test");
    }

    @Test
    void createRequest() throws Exception {
        UserDto userDtoCreate = userController.createUser(userDto1);
        RequestDto itemRequestDtoCreate =
                itemRequestController.createRequest(userDtoCreate.getId(), requestDto);

        assertThat(itemRequestDtoCreate.getId(), equalTo(1L));
    }

    @Test
    void getRequests() throws Exception {

        UserDto userDtoCreate = userController.createUser(userDto1);
        RequestDto itemRequestDtoCreate =
                itemRequestController.createRequest(userDtoCreate.getId(), requestDto);

        assertThat(1, equalTo(itemRequestController.getRequests(1).size()));
        assertThat(1, equalTo(itemRequestController.getCurrentCountOfRequests(2, 0, 1).size()));
        assertThat("test", equalTo(itemRequestController.getRequestById(1, 1).getDescription()));
    }
}