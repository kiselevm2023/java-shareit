package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.RequestComment;
import ru.practicum.shareit.comment.dto.ResponseComment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final UserService userService;
    private final BookingService bookingService;

    UserDto userDto1 = new UserDto();
    UserDto userDto2 = new UserDto();
    CreateItemDto itemDto1 = new CreateItemDto();
    CreateItemDto itemDto2 = new CreateItemDto();
    CreateItemDto itemDto3 = new CreateItemDto();
    ItemDto itemDto4 = new ItemDto();
    ItemDto itemDto5 = new ItemDto();
    CreateItemDto itemDto6 = new CreateItemDto();
    RequestComment commentDto1 = new RequestComment();

    @BeforeEach
    void setUp() {
        userDto1.setName("testName1");
        userDto1.setEmail("test@mail.ru");
        userDto2.setName("update");
        userDto2.setEmail("update@mail.ru");
        itemDto1.setAvailable(true);
        itemDto1.setDescription("test1Desc");
        itemDto1.setName("test");
        itemDto2.setAvailable(true);
        itemDto2.setDescription("update");
        itemDto2.setName("update");
        itemDto3.setName("update");
        itemDto4.setDescription("update6");
        itemDto5.setAvailable(false);
        itemDto6.setDescription("newDescOnly");
        commentDto1.setText("test");
    }

    @Test
    void createItem() {
        UserDto userDtoCreate = userService.createUser(userDto1);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate.getId(), itemDto1);
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto1.getName()).getSingleResult();

        assertThat(item.getName(), equalTo(itemDto1.getName()));
        assertThat(item.getDescription(), equalTo(itemDto1.getDescription()));
        assertThat(item.getOwner(), equalTo(UserMapper.toUser(userDtoCreate)));
    }

    @Test
    void createItemWithWrongUserId() {
        UserDto userDtoCreate = userService.createUser(userDto1);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate.getId(), itemDto1);
        NotFoundException validationExceptionWithWrongId = assertThrows(NotFoundException.class,
                () -> itemService
                        .createItem(userDtoCreate.getId() + 10, itemDto1));

        assertThat(validationExceptionWithWrongId.getMessage(),
                equalTo("Пользователь не найден"));
    }

    @Test
    void updateItem() {
        UserDto userDto = userService.createUser(userDto1);
        ItemDto itemDto = itemService.createItem(userDto.getId(), itemDto1);
        ItemDto itemDtoUpdate1 = itemService.updateItem(userDto.getId(), itemDto2, itemDto.getId());
        TypedQuery<Item> query1 = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        Item item1 = query1.setParameter("id", itemDtoUpdate1.getId()).getSingleResult();

        assertThat(item1.getName(), equalTo(itemDtoUpdate1.getName()));
        assertThat(item1.getDescription(), equalTo(itemDtoUpdate1.getDescription()));
        assertThat(item1.getOwner(), equalTo(UserMapper.toUser(userDto)));
        assertThat(item1.getAvailable(), equalTo(itemDtoUpdate1.getAvailable()));

        ItemDto itemDtoUpdate2 = itemService.updateItem(userDto.getId(), itemDto3, itemDto.getId());
        TypedQuery<Item> query2 = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        Item item2 = query2.setParameter("id", itemDtoUpdate2.getId()).getSingleResult();

        assertThat(item2.getName(), equalTo(itemDtoUpdate2.getName()));
        assertThat(item2.getDescription(), equalTo(itemDtoUpdate2.getDescription()));
        assertThat(item2.getOwner(), equalTo(UserMapper.toUser(userDto)));
        assertThat(item2.getAvailable(), equalTo(itemDtoUpdate2.getAvailable()));

        ItemDto itemDtoUpdate3 = itemService.updateItem(userDto.getId(), itemDto6, itemDto.getId());
        TypedQuery<Item> query3 = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        Item item3 = query3.setParameter("id", itemDtoUpdate3.getId()).getSingleResult();

        assertThat(item3.getName(), equalTo(itemDtoUpdate3.getName()));
        assertThat(item3.getDescription(), equalTo(itemDtoUpdate3.getDescription()));
        assertThat(item3.getOwner(), equalTo(UserMapper.toUser(userDto)));
        assertThat(item3.getAvailable(), equalTo(itemDtoUpdate3.getAvailable()));

    }

    @Test
    void updateItemWithWrongItemId() {
        UserDto userDtoCreate = userService.createUser(userDto1);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate.getId(), itemDto1);

        NotFoundException notFoundExceptionWithWrongIdItem = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(userDtoCreate.getId(), itemDto2, itemDtoCreate.getId() + 5));
        assertThat(notFoundExceptionWithWrongIdItem.getMessage(),
                equalTo("Вещь с данным id не найдена"));
    }

    @Test
    void updateItemWithWrongIUserId() {
        UserDto userDtoCreate = userService.createUser(userDto1);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate.getId(), itemDto1);

        NotFoundException notFoundExceptionWithWrongIdUser = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(userDtoCreate.getId() + 5, itemDto2, itemDtoCreate.getId()));
        assertThat(notFoundExceptionWithWrongIdUser.getMessage(),
                equalTo("Пользователь не найден"));
    }

    @Test
    void updateItemWithIncorrectUserId() {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate1.getId(), itemDto1);

        NotFoundException notFoundExceptionWithWrongIdUser = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(userDtoCreate2.getId(), itemDto2, itemDtoCreate.getId()));
        assertThat(notFoundExceptionWithWrongIdUser.getMessage(),
                equalTo("У пользователя с id = " + userDtoCreate2.getId() +
                        " нет вещи с id = " + itemDtoCreate.getId()));
    }

    @Test
    void createComment() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate.getId(), true);
        TimeUnit.SECONDS.sleep(4);
        ResponseComment commentDto = itemService.createComment(userDtoCreate1.getId(), commentDto1, itemDtoCreate.getId());
        TypedQuery<Comment> query =
                em.createQuery("SELECT c FROM Comment c WHERE c.authorName = :authorName", Comment.class);
        Comment comment = query.setParameter("authorName", userDtoCreate1.getName()).getSingleResult();

        assertThat(comment.getCreated().truncatedTo(ChronoUnit.MINUTES)
                .compareTo(commentDto.getCreated().truncatedTo(ChronoUnit.MINUTES)), equalTo(0));
        assertThat(comment.getText(), equalTo(commentDto.getText()));
        assertThat(comment.getId(), equalTo(commentDto.getId()));
    }

    @Test
    void createCommentWithWrongUserId() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate.getId(), true);

        NotFoundException validationExceptionWithWrongId = assertThrows(NotFoundException.class,
                () -> itemService.createComment(userDtoCreate1.getId() + 10, commentDto1, itemDtoCreate.getId()));
        assertThat(validationExceptionWithWrongId.getMessage(),
                equalTo("Пользователь не найден"));
    }

    @Test
    void createCommentWithWrongItemId() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate.getId(), true);

        NotFoundException notFoundExceptionWithWrongIdItem = assertThrows(NotFoundException.class,
                () -> itemService.createComment(userDtoCreate1.getId(), commentDto1, itemDtoCreate.getId() + 10));
        assertThat(notFoundExceptionWithWrongIdItem.getMessage(),
                equalTo("Вещь с данным id не найдена"));
    }

    @Test
    void createCommentWithIncorrectUserId() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate.getId(), true);

        ValidationException validationExceptionIncorrectIdUser = assertThrows(ValidationException.class,
                () -> itemService.createComment(userDtoCreate2.getId(), commentDto1, itemDtoCreate.getId()));
        assertThat(validationExceptionIncorrectIdUser.getMessage(),
                equalTo("Пользователь не бронировал эту вещь"));
    }

    @Test
    void getItemById() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate.getId(), true);

        ItemDto itemDtoGet1 = itemService.getItemById(itemDtoCreate.getId(), userDtoCreate1.getId());
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoCreate.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(itemDtoGet1.getId()));
        assertThat(item.getDescription(), equalTo(itemDtoGet1.getDescription()));
        assertThat(item.getName(), equalTo(itemDtoGet1.getName()));
        ;
    }

    @Test
    void getItemByIdNextBooking() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);

        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate.getId(), true);
        TimeUnit.SECONDS.sleep(4);

        ResponseComment  commentDto = itemService.createComment(userDtoCreate1.getId(), commentDto1, itemDtoCreate.getId());
        ItemDto itemDtoGet1 = itemService.getItemById(itemDtoCreate.getId(), userDtoCreate1.getId());
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoCreate.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(itemDtoGet1.getId()));
        assertThat(item.getDescription(), equalTo(itemDtoGet1.getDescription()));
        assertThat(item.getName(), equalTo(itemDtoGet1.getName()));

    }

    @Test
    void getItemByIdCurrentBooking() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate.getId(), true);
        TimeUnit.SECONDS.sleep(4);
        ResponseComment  commentDto = itemService.createComment(userDtoCreate1.getId(), commentDto1, itemDtoCreate.getId());
        ItemDto itemDtoGet1 = itemService.getItemById(itemDtoCreate.getId(), userDtoCreate1.getId());
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoCreate.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(itemDtoGet1.getId()));
        assertThat(item.getDescription(), equalTo(itemDtoGet1.getDescription()));
        assertThat(item.getName(), equalTo(itemDtoGet1.getName()));
    }

    @Test
    void getItemByIdWithEmptyBooking() throws Exception {
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        ItemDto itemDtoGet1 = itemService.getItemById(itemDtoCreate.getId(), userDtoCreate2.getId());

        assertThat(itemDtoGet1.getId(), equalTo(itemDtoCreate.getId()));
    }

    @Test
    void getItemByIdWrongItemId() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate.getId(), true);
        TimeUnit.SECONDS.sleep(4);
        ResponseComment commentDto = itemService.createComment(userDtoCreate1.getId(), commentDto1, itemDtoCreate.getId());

        NotFoundException notFoundExceptionIncorrectIdItem = assertThrows(NotFoundException.class,
                () -> itemService.createComment(userDtoCreate1.getId(), commentDto1, itemDtoCreate.getId() + 5));
        assertThat(notFoundExceptionIncorrectIdItem.getMessage(),
                equalTo("Вещь с данным id не найдена"));
    }

    @Test
    void getItemByOwner() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate.getId(), true);
        TimeUnit.SECONDS.sleep(4);
        ResponseComment commentDto = itemService.createComment(userDtoCreate1.getId(), commentDto1, itemDtoCreate.getId());
        List<ItemDto> itemDtos = itemService.getAllItemForOwner(0, 1, userDtoCreate2.getId());
        TypedQuery<Item> query1 = em.createQuery("SELECT i FROM Item i WHERE i.owner.id = :id", Item.class);
        List<Item> items = query1.setParameter("id", userDtoCreate2.getId()).getResultList();

        assertThat(itemDtos.size(), equalTo(items.size()));
        assertThat(itemDtos.get(0).getId(), equalTo(items.get(0).getId()));
    }

    @Test
    void getItemByOwnerWithOtherBooking() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto1 = new CreateBookingDto();
        bookingDto1.setItemId(itemDtoCreate.getId());
        bookingDto1.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto1.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto1, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true);
        TimeUnit.SECONDS.sleep(4);
        List<ItemDto> itemDtos = itemService.getAllItemForOwner(0, 1, userDtoCreate2.getId());
        TypedQuery<Item> query1 = em.createQuery("SELECT i FROM Item i WHERE i.owner.id = :id", Item.class);
        List<Item> items = query1.setParameter("id", userDtoCreate2.getId()).getResultList();

        assertThat(itemDtos.size(), equalTo(items.size()));
        assertThat(itemDtos.get(0).getId(), equalTo(items.get(0).getId()));
    }

    @Test
    void getItemByOwnerWithCurrentBooking() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto1 = new CreateBookingDto();
        bookingDto1.setItemId(itemDtoCreate.getId());
        bookingDto1.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto1.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate1 = bookingService.createBooking(bookingDto1, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate1.getId(), true);
        TimeUnit.SECONDS.sleep(4);
        ResponseComment commentDto = itemService.createComment(userDtoCreate1.getId(), commentDto1, itemDtoCreate.getId());
        List<ItemDto> itemDtos = itemService.getAllItemForOwner(0, 1, userDtoCreate2.getId());
        TypedQuery<Item> query1 = em.createQuery("SELECT i FROM Item i WHERE i.owner.id = :id", Item.class);
        List<Item> items = query1.setParameter("id", userDtoCreate2.getId()).getResultList();

        assertThat(itemDtos.size(), equalTo(items.size()));
        assertThat(itemDtos.get(0).getId(), equalTo(items.get(0).getId()));
    }

    @Test
    void getItemByOwnerWrongUserId() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate.getId(), true);
        TimeUnit.SECONDS.sleep(4);
        ResponseComment commentDto = itemService.createComment(userDtoCreate1.getId(), commentDto1, itemDtoCreate.getId());

        NotFoundException notFoundExceptionIncorrectIdUser = assertThrows(NotFoundException.class,
                () -> itemService.getAllItemForOwner(0, 1, userDtoCreate2.getId() + 10));
        assertThat(notFoundExceptionIncorrectIdUser.getMessage(),
                equalTo("Пользователь с id = " + (userDtoCreate2.getId() + 10) + " не найден"));
    }

    @Test
    void getItemByOwnerWithoutBooking() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        List<ItemDto> itemDtos = itemService.getAllItemForOwner(0, 1, userDtoCreate2.getId());
        TypedQuery<Item> query1 = em.createQuery("SELECT i FROM Item i WHERE i.owner.id = :id", Item.class);
        List<Item> items = query1.setParameter("id", userDtoCreate2.getId()).getResultList();

        assertThat(items.size(), equalTo(itemDtos.size()));
        assertThat(items.get(0).getName(), equalTo(itemDtos.get(0).getName()));
    }

    @Test
    void getItemForBooker() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate1 = itemService.createItem(userDtoCreate1.getId(), itemDto1);
        ItemDto itemDtoCreate2 = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate2.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate.getId(), true);
        TimeUnit.SECONDS.sleep(4);
        ResponseComment commentDto = itemService.createComment(userDtoCreate1.getId(), commentDto1, itemDtoCreate2.getId());
        List<ItemDto> itemDtos = itemService.getItemForBooker("test", userDtoCreate1.getId(), 0, 1);
        TypedQuery<Item> query1 = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        List<Item> items = query1.setParameter("id", itemDtoCreate1.getId()).getResultList();

        assertThat(items.size(), equalTo(itemDtos.size()));
        assertThat(items.get(0).getDescription(), equalTo(itemDtos.get(0).getDescription()));
        assertThat(items.get(0).getName(), equalTo(itemDtos.get(0).getName()));
    }

    @Test
    void getItemForBookerWithNoSearch() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate.getId(), true);
        TimeUnit.SECONDS.sleep(4);
        ResponseComment commentDto = itemService.createComment(userDtoCreate1.getId(), commentDto1, itemDtoCreate.getId());
        List<ItemDto> itemDtos = itemService.getItemForBooker("", userDtoCreate1.getId(), 0, 1);

        assertThat(0, equalTo(itemDtos.size()));
    }

    @Test
    void getItemForBookerWithWrongUserId() throws Exception {
        UserDto userDtoCreate1 = userService.createUser(userDto1);
        UserDto userDtoCreate2 = userService.createUser(userDto2);
        ItemDto itemDtoCreate = itemService.createItem(userDtoCreate2.getId(), itemDto2);
        CreateBookingDto bookingDto = new CreateBookingDto();
        bookingDto.setItemId(itemDtoCreate.getId());
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoCreate = bookingService.createBooking(bookingDto, userDtoCreate1.getId());
        bookingService.approvedBooking(userDtoCreate2.getId(), bookingDtoCreate.getId(), true);
        TimeUnit.SECONDS.sleep(4);
        ResponseComment commentDto = itemService.createComment(userDtoCreate1.getId(), commentDto1, itemDtoCreate.getId());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.getItemForBooker("test", userDtoCreate1.getId() + 10, 0, 1));
        assertThat(notFoundException.getMessage(),
                equalTo("Пользователь с id = " + (userDtoCreate1.getId() + 10) + " не найден"));
    }
}