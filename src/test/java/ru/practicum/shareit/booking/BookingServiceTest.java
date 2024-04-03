package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.RequestComment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final BookingServiceImpl bookingService;

    UserDto userDto1 = new UserDto();
    UserDto userDto2 = new UserDto();
    CreateItemDto itemDto1 = new CreateItemDto();
    CreateItemDto itemDto2 = new CreateItemDto();

    @BeforeEach
    void setUp() {
        userDto1.setName("Test");
        userDto1.setEmail("test@mail.ru");
        userDto2.setName("update");
        userDto2.setEmail("update@mail.ru");
        itemDto1.setAvailable(true);
        itemDto1.setDescription("TestDesc");
        itemDto1.setName("Test");
        itemDto2.setAvailable(true);
        itemDto2.setDescription("update");
        itemDto2.setName("update");
    }

    @Test
    void getBookingForOwnerWithOtherParameters() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto1 = new CreateBookingDto();
        bookingDto1.setItemId(itemDtoCreate.getId());
        bookingDto1.setStart(LocalDateTime.now().plusSeconds(2));
        bookingDto1.setEnd(LocalDateTime.now().plusSeconds(3));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto1, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true);
        TimeUnit.SECONDS.sleep(4);
        TypedQuery<Booking> query =
                em.createQuery("SELECT b FROM Booking b WHERE b.item.owner.id = :id", Booking.class);
        List<Booking> bookings = query.setParameter("id", userDtoCreate2.getId()).getResultList();
        List<BookingDto> bookingDtos1 = bookingService
                .getAllBookingForOwner(userDtoCreate2.getId(), "ALL", 0, 1);

        assertThat(bookings.size(), equalTo(bookingDtos1.size()));
        assertThat(bookings.get(0).getId(), equalTo(bookingDtos1.get(0).getId()));
        List<BookingDto> bookingDtos2 = bookingService
                .getAllBookingForOwner(userDtoCreate2.getId(), "PAST", 0, 1);
        assertThat(bookings.size(), equalTo(bookingDtos2.size()));
        List<BookingDto> bookingDtos3 = bookingService
                .getAllBookingForOwner(userDtoCreate2.getId(), "CURRENT", 0, 1);
        assertThat(bookingDtos3.size(), equalTo(0));
        List<BookingDto> bookingDtos4 = bookingService
                .getAllBookingForOwner(userDtoCreate2.getId(), "FUTURE", 0, 1);
        assertThat(bookingDtos4.size(), equalTo(1));
        List<BookingDto> bookingDtos5 = bookingService
                .getAllBookingForOwner(userDtoCreate2.getId(), "WAITING", 0, 1);
        assertThat(bookingDtos5.size(), equalTo(0));
        List<BookingDto> bookingDtos6 = bookingService
                .getAllBookingForOwner(userDtoCreate2.getId(), "REJECTED", 0, 1);
        assertThat(bookingDtos6.size(), equalTo(0));
        ValidationException validationExceptionIncorrectState = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingForOwner(userDtoCreate2.getId(), "Al", 0, 1));
        assertThat(validationExceptionIncorrectState.getMessage(),
                equalTo("Unknown state: UNSUPPORTED_STATUS"));
    }

    @Test
    void createBookingWithIncorrectTime() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDtoWrong = new CreateBookingDto();
        bookingDtoWrong.setItemId(itemDtoCreate.getId());
        bookingDtoWrong.setStart(LocalDateTime.now());
        bookingDtoWrong.setEnd(LocalDateTime.now().plusSeconds(2));
        ValidationException validationExceptionIncorrectTime = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDtoWrong, userDtoCreate1.getId()));
        assertThat(validationExceptionIncorrectTime.getMessage(),
                equalTo("Неверные параметры для времени, проверьте правильность запроса"));
    }

    @Test
    void itemEmptyComment() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto1 = new CreateBookingDto();
        bookingDto1.setItemId(itemDtoCreate.getId());
        bookingDto1.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto1.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto1, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true);
        RequestComment commentDto = new RequestComment();
        commentDto.setText("");
        TimeUnit.SECONDS.sleep(3);

        ValidationException emptyComment = assertThrows(ValidationException.class,
                () -> itemService.createComment(1, commentDto, 1));
        assertThat(emptyComment.getMessage(),
                equalTo("Пустой комментарий"));
    }

    @Test
    void createBookingWithIncorrectUserId() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto  bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        NotFoundException notFoundExceptionIncorrectUserId = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(bookingDto, userDtoCreate1.getId() + 10));
        assertThat(notFoundExceptionIncorrectUserId.getMessage(),
                equalTo("Пользователь не найден"));
    }

    @Test
    void createBookingWithIncorrectItemId() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto  bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId() + 5);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));

        NotFoundException notFoundExceptionIncorrectItemId = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(bookingDto, userDtoCreate1.getId()));
        assertThat(notFoundExceptionIncorrectItemId.getMessage(),
                equalTo("Вещь с данным id не найдена"));
    }

    @Test
    void createBookingWithNotAvailableItem() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        itemDto2.setAvailable(false);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto  bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));

        ValidationException validationExceptionIncorrectTime = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto, userDtoCreate1.getId()));
        assertThat(validationExceptionIncorrectTime.getMessage(),
                equalTo("Вещь недоступна"));
    }

    @Test
    void createBookingWithBookedItem() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto1 = new CreateBookingDto();
        bookingDto1.setItemId(itemDtoCreate.getId());
        bookingDto1.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto1.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto1, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true);
        CreateBookingDto  bookingDto2 = new CreateBookingDto ();
        bookingDto2.setItemId(itemDtoCreate.getId());
        bookingDto2.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto2.setEnd(LocalDateTime.now().plusSeconds(2));

        ValidationException validationExceptionIncorrectTime = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto2, userDtoCreate1.getId()));
        assertThat(validationExceptionIncorrectTime.getMessage(),
                equalTo("Вещь занята"));
    }

    @Test
    void approvedBooking() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true);
        TypedQuery<Booking> query =
                em.createQuery("SELECT b FROM Booking b WHERE b.status = :status", Booking.class);
        List<Booking> bookings = query.setParameter("status", Status.APPROVED).getResultList();

        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    void cancelledBooking() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto  bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate1.getId(), bookingDtoCreate1.getId(), false);
        TypedQuery<Booking> query =
                em.createQuery("SELECT b FROM Booking b WHERE b.status = :status", Booking.class);
        List<Booking> bookings = query.setParameter("status", Status.CANCELED).getResultList();

        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    void rejectedBooking() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto  bookingDto = new CreateBookingDto ();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), false);
        TypedQuery<Booking> query =
                em.createQuery("SELECT b FROM Booking b WHERE b.status = :status", Booking.class);
        List<Booking> bookings = query.setParameter("status", Status.REJECTED).getResultList();

        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    void approvedBookingWithWrongUserId() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto  bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto, userDtoCreate1.getId());

        NotFoundException validationExceptionIncorrectUserId = assertThrows(NotFoundException.class,
                () -> bookingService
                        .approvedBooking(userDtoCreate2.getId() + 5, bookingDtoCreate1.getId(), true));
        assertThat(validationExceptionIncorrectUserId.getMessage(),
                equalTo("Пользователь с id = " + (userDtoCreate2.getId() + 5) + " не найден"));
    }

    @Test
    void approvedBookingWithWrongBookingId() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto  bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto, userDtoCreate1.getId());

        NotFoundException validationExceptionIncorrectBookingId = assertThrows(NotFoundException.class,
                () -> bookingService
                        .approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId() + 5, true));
        assertThat(validationExceptionIncorrectBookingId.getMessage(),
                equalTo("Бронирование не найдено"));
    }

    @Test
    void approvedBookingWithDoubleTime() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto  bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true);

        ValidationException validationExceptionDoubleTime = assertThrows(ValidationException.class,
                () -> bookingService
                        .approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true));
        assertThat(validationExceptionDoubleTime.getMessage(),
                equalTo("Статус у данного бронирования уже изменен"));
    }

    @Test
    void approvedBookingWithIncorrectUserId() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        UserDto userDto3 = new UserDto();
        userDto3.setName("test3");
        userDto3.setEmail("test3@mail3.ru");
        UserDto userDtoCreate3 = userService.createUser(userDto3);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto, userDtoCreate1.getId());

        NotFoundException validationExceptionIncorrectBookingId = assertThrows(NotFoundException.class,
                () -> bookingService
                        .approvedBooking(userDtoCreate3.getId(), bookingDtoCreate1.getId(), true));
        assertThat(validationExceptionIncorrectBookingId.getMessage(),
                equalTo("Бронирование не найдено"));
    }

    @Test
    void approvedBookingWithBookerId() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto, userDtoCreate1.getId());

        NotFoundException validationExceptionWithBookerId = assertThrows(NotFoundException.class,
                () -> bookingService
                        .approvedBooking(userDtoCreate1.getId(), bookingDtoCreate1.getId(), true));
        assertThat(validationExceptionWithBookerId.getMessage(),
                equalTo("Вы не можете одобрить данный запрос"));
    }

    @Test
    void getBookingForUserWithOtherParameters() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto1 = new CreateBookingDto();
        bookingDto1.setItemId(itemDtoCreate.getId());
        bookingDto1.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto1.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto1, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true);
        TimeUnit.SECONDS.sleep(4);
        TypedQuery<Booking> query =
                em.createQuery("SELECT b FROM Booking b WHERE b.booker.id = :id", Booking.class);
        List<Booking> bookings = query.setParameter("id", userDtoCreate1.getId()).getResultList();
        List<BookingDto> bookingDtos1 = bookingService
                .getAllBookingForUser(userDtoCreate1.getId(), "ALL", 0, 1);
        assertThat(bookings.size(), equalTo(bookingDtos1.size()));
        assertThat(bookings.get(0).getId(), equalTo(bookingDtos1.get(0).getId()));
        List<BookingDto> bookingDtos2 = bookingService
                .getAllBookingForUser(userDtoCreate1.getId(), "PAST", 0, 1);
        assertThat(bookings.size(), equalTo(bookingDtos2.size()));
        List<BookingDto> bookingDtos3 = bookingService
                .getAllBookingForUser(userDtoCreate1.getId(), "CURRENT", 0, 1);
        assertThat(bookingDtos3.size(), equalTo(0));
        List<BookingDto> bookingDtos4 = bookingService
                .getAllBookingForUser(userDtoCreate1.getId(), "FUTURE", 0, 1);
        assertThat(bookingDtos4.size(), equalTo(1));
        List<BookingDto> bookingDtos5 = bookingService
                .getAllBookingForUser(userDtoCreate1.getId(), "WAITING", 0, 1);
        assertThat(bookingDtos5.size(), equalTo(0));
        List<BookingDto> bookingDtos6 = bookingService
                .getAllBookingForUser(userDtoCreate1.getId(), "REJECTED", 0, 1);
        assertThat(bookingDtos6.size(), equalTo(0));
        ValidationException validationExceptionIncorrectState = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingForUser(userDtoCreate1.getId(), "OLL", 0, 1));
        assertThat(validationExceptionIncorrectState.getMessage(),
                equalTo("Unknown state: UNSUPPORTED_STATUS"));
    }

    @Test
    void getBookingById() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto1 = new CreateBookingDto();
        bookingDto1.setItemId(itemDtoCreate.getId());
        bookingDto1.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto1.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto1, userDtoCreate1.getId());
        TypedQuery<Booking> query =
                em.createQuery("SELECT b FROM Booking b WHERE b.id = :id", Booking.class);
        Booking bookings = query.setParameter("id", bookingDtoCreate1.getId()).getSingleResult();
        BookingDto bookingDto = bookingService.getBookingById(userDtoCreate1.getId(), bookingDtoCreate1.getId());
        assertThat(bookings.getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void getBookingByIdWrongIdUser() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        UserDto userDto3 = new UserDto();
        userDto3.setName("test3");
        userDto3.setEmail("test3@test3.ru");
        UserDto userDtoCreate3 = userService.createUser(userDto3);
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto1 = new CreateBookingDto();
        bookingDto1.setItemId(itemDtoCreate.getId());
        bookingDto1.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto1.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto1, userDtoCreate1.getId());
        NotFoundException validationExceptionIncorrectUserId = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(userDtoCreate1.getId() + 5, bookingDtoCreate1.getId()));
        assertThat(validationExceptionIncorrectUserId.getMessage(),
                equalTo("Пользователь с id = " + (userDtoCreate1.getId() + 5) + " не найден"));
    }

    @Test
    void getBookingByIdIncorrectIdUser() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        UserDto userDto3 = new UserDto();
        userDto3.setName("test3");
        userDto3.setEmail("test3@mail3.ru");
        UserDto userDtoCreate3 = userService.createUser(userDto3);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto1 = new CreateBookingDto();
        bookingDto1.setItemId(itemDtoCreate.getId());
        bookingDto1.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto1.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto1, userDtoCreate1.getId());

        NotFoundException validationExceptionIncorrectUserId = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(userDtoCreate3.getId(), bookingDtoCreate1.getId()));
        assertThat(validationExceptionIncorrectUserId.getMessage(),
                equalTo("Бронирование не найдено"));
    }

    @Test
    void getBookingByIdIncorrectIdBooking() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto1 = new CreateBookingDto();
        bookingDto1.setItemId(itemDtoCreate.getId());
        bookingDto1.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto1.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto1, userDtoCreate1.getId());

        NotFoundException validationExceptionIncorrectBookingId = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(userDtoCreate1.getId(), bookingDtoCreate1.getId() + 5));
        assertThat(validationExceptionIncorrectBookingId.getMessage(),
                equalTo("Бронирование не найдено"));
    }
}