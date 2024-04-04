package ru.practicum.shareit.user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@user.com");
    }

    @Test
    void getUserById() throws Exception {
        //User user = new User();
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        UserDto userDto = userService.getUserById(1L);

        assertThat(userDto.getName(), equalTo(user.getName()));
    }

    @Test
    void getUserByWrongId() throws Exception {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.getUserById(1L));

        assertThat(notFoundException.getMessage(),
                equalTo("Пользователь не найден"));
    }

}