package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    private final EntityManager em;
    private final UserController userController;

    UserDto userDto1 = new UserDto();
    UserDto userDto2 = new UserDto();

    @BeforeEach
    void setUp() {
        userDto1.setName("Test1");
        userDto1.setEmail("test@mail.ru");
        userDto2.setName("Test2");
    }

    @Test
    void createUser() throws Exception {
        UserDto userDtoCreate = userController.createUser(userDto1);

        assertThat(userDtoCreate.getName(), equalTo(userController.getUserById(1L).getName()));
    }

    @Test
    void createUserWithNoEmail() throws Exception {
        DataIntegrityViolationException conflictException = assertThrows(DataIntegrityViolationException.class,
                () -> userController.createUser(userDto2));
        assertThat(conflictException.getMessage(),
                equalTo("could not execute statement; SQL [n/a]; constraint [null];" +
                        " nested exception is org.hibernate.exception.ConstraintViolationException: " +
                        "could not execute statement"));
    }

    @Test
    void getUsers() throws Exception {

        UserDto userDtoCreate = userController.createUser(userDto1);

        assertThat(userController.getUsers().size(), equalTo(1));
    }

    @Test
    void updateUserAndDelete() throws Exception {

        UserDto userDtoCreate = userController.createUser(userDto1);
        userDto2.setName("update");
        userDto2.setEmail("update@mail.ru");
        userController.updateUser(userDto2, 1L);

        assertThat("update", equalTo(userController.getUserById(1L).getName()));

        userController.deleteUser(1L);
        assertThat(0, equalTo(userController.getUsers().size()));
    }

}