package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.RequestServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;


import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceTestMock {
    @Mock
    RequestRepository requestRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    private RequestServiceImpl itemRequestService;
    @Autowired
    private MockMvc mvc;
    private User user = new User();

    @BeforeEach
    void setUp() {
        user.setEmail("test@mail.ru");
        user.setName("testName");
        user.setId(1);
    }

    @Test
    void findItemRequestByIdWrongIdItemRequest() throws Exception {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(requestRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Запроса с id = 1 не существует"));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(1, 1));
        assertThat(notFoundException.getMessage(),
                equalTo("Запроса с id = 1 не существует"));
    }

    @Test
    void findItemRequestByIdWrongIdUser() throws Exception {
        user.setId(2);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(requestRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь с id = 1 не найден"));
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(1, 1));
        assertThat(notFoundException.getMessage(),
                equalTo("Пользователь с id = 1 не найден"));
    }

    @Test
    void findAllItemRequestWithWrongIdUser() throws Exception {
        user.setId(2);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(requestRepository.findAllExceptOwner(Mockito.any(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь с id = 1 не найден"));
        userRepository.findById(1L);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getCurrentCountOfRequests(1, 1, 1));
        assertThat(notFoundException.getMessage(),
                equalTo("Пользователь с id = 1 не найден"));
    }
}