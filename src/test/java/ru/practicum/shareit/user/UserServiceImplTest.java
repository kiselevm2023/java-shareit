package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {


    private final EntityManager em;
    private final UserService userService;
    UserDto userDto1 = new UserDto();
    UserDto userDto2 = new UserDto();
    UserDto userDto3 = new UserDto();
    UserDto userDto4 = new UserDto();

    @BeforeEach
    void setUp() {
        userDto1.setName("test");
        userDto1.setEmail("test@mail.ru");
        userDto2.setEmail("update@mail.ru");
        userDto3.setName("test3");
        userDto4.setName("test4");
        userDto4.setEmail("test4@test4.ru");
    }

    @Test
    void createUser() {
        userService.createUser(userDto1);
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query.setParameter("email", userDto1.getEmail()).getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto1.getName()));
        assertThat(user.getEmail(), equalTo(userDto1.getEmail()));
        List<UserDto> userDtos = userService.getUsers();
        TypedQuery<User> query1 = em.createQuery("SELECT u FROM User u", User.class);
        List<User> users = query1.getResultList();

        assertThat(users.size(), equalTo(userDtos.size()));
        assertThat(users.get(0).getId(), equalTo(userDtos.get(0).getId()));
    }

    @Test
    void createAlreadyUsedEmail() {
        userService.createUser(userDto1);
        DataIntegrityViolationException dataIntegrityViolationException = assertThrows(DataIntegrityViolationException.class,
                () -> userService.createUser(userDto1));
        assertThat(dataIntegrityViolationException.getMessage(),
                equalTo("could not execute statement; SQL [n/a]; " +
                        "constraint [null]; " +
                        "nested exception is org.hibernate.exception.ConstraintViolationException: " +
                        "could not execute statement"));
    }

    @Test
    void createUserWithEmptyEmail() {
        UserDto userDto = new UserDto();
        userDto.setEmail("");
        DataIntegrityViolationException conflictException
                = assertThrows(DataIntegrityViolationException.class,
                () -> userService.createUser(userDto));
        assertThat(conflictException.getMessage(), equalTo("could not execute statement; SQL [n/a]; " +
                "constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: " +
                "could not execute statement"));
    }

    @Test
    void getUserById() {
        userService.createUser(userDto1);
        UserDto userDto = userService.getUserById(1L);
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query.setParameter("email", userDto1.getEmail()).getSingleResult();

        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        assertThat(user.getId(), equalTo(userDto.getId()));

        try {
            userService.getUserById(100L);
        } catch (NotFoundException e) {
            assertThat(e.getMessage(), equalTo("Пользователь с id = 100 не найден"));
        }
    }

    @Test
    void updateAndDeleteUser() {
        UserDto userDto = userService.createUser(userDto1);
        userService.updateUser(userDto2, 1L);
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getEmail(), equalTo(userDto2.getEmail()));

        UserDto userDto5 = userService.updateUser(userDto3, userDto.getId());

        assertThat(userDto5.getName(), equalTo(userDto3.getName()));

        UserDto userDto6 = userService.updateUser(userDto4, userDto.getId());

        assertThat(userDto6.getName(), equalTo(userDto4.getName()));

        userService.deleteUser(userDto.getId());

        assertThat(userService.getUsers().size(), equalTo(0));
    }
}