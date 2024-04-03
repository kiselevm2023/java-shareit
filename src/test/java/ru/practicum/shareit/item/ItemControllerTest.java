package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @MockBean
    ItemServiceImpl itemService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final CreateItemDto itemDto1 = new CreateItemDto();
    private final ItemDto itemDto2 = new ItemDto();
    private final CommentDto commentDto = new CommentDto();
    private final UserDto userDto = new UserDto();
    private final List<CreateItemDto> itemDtos = new ArrayList<>();
    private Item item = new Item();
    private User user = new User();

    private final ItemDto item2= new ItemDto();


    @BeforeEach
    void setUp() {
        itemDto1.setId(1);
        itemDto1.setDescription("TestItem1Desc");
        itemDto1.setName("TestItem1Name");
        itemDto1.setAvailable(true);
        itemDto2.setId(1);
        itemDto2.setDescription(null);
        itemDto2.setName("TestItem2Name");
        itemDto2.setAvailable(true);
        userDto.setEmail("test@mail.ru");
        userDto.setId(1);
        userDto.setName("Test");
        item = ItemMapper.createToItem(itemDto1, user);
        user = UserMapper.toUser(userDto);
        commentDto.setCreated(LocalDateTime.now().withNano(0));
        commentDto.setText("Test comment");
        commentDto.setId(1L);
        commentDto.setAuthorName("Test name");

        /*ItemDto item = new ItemDto();
        item.setId(comment.getItem().getId());
        item.setName(comment.getItem().getName());
        commentResponseDto.setItem(item);*/

        commentDto.setItem(item2);
        itemDtos.add(itemDto1);
    }

    @Test
    void createItem() throws Exception {
        Mockito.when(itemService.createItem(Mockito.anyLong(), Mockito.any())).thenReturn(itemDto1);
        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto1.getId()))
                .andExpect(jsonPath("$.description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$.name").value(itemDto1.getName()))
                .andExpect(jsonPath("$.available").value(itemDto1.getAvailable()));
    }

    @Test
    void createComment() throws Exception {
        Mockito.when(itemService.createComment(Mockito.anyLong(), Mockito.any(), Mockito.anyLong()))
                .thenReturn(commentDto);
        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));
    }

    @Test
    void getItemById() throws Exception {
        Mockito.when(itemService.getItemById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemDto1);
        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto1.getId()))
                .andExpect(jsonPath("$.description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$.name").value(itemDto1.getName()))
                .andExpect(jsonPath("$.available").value(itemDto1.getAvailable()));
    }

    @Test
    void getItemByOwner() throws Exception {
        Mockito.when(itemService.getAllItemForOwner(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
                .thenReturn(itemDtos);
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]id").value(itemDto1.getId()))
                .andExpect(jsonPath("$[0]description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$[0]name").value(itemDto1.getName()))
                .andExpect(jsonPath("$[0]available").value(itemDto1.getAvailable()));
    }

    @Test
    void getItemForBooker() throws Exception {
        Mockito.when(itemService
                        .getItemForBooker(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemDtos);
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]id").value(itemDto1.getId()))
                .andExpect(jsonPath("$[0]description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$[0]name").value(itemDto1.getName()))
                .andExpect(jsonPath("$[0]available").value(itemDto1.getAvailable()));
    }

    @Test
    void updateItem() throws Exception {
        Mockito.when(itemService
                        .updateItem(Mockito.anyLong(), Mockito.any(), Mockito.anyLong()))
                .thenReturn(itemDto1);
        mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto1.getId()))
                .andExpect(jsonPath("$.description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$.name").value(itemDto1.getName()))
                .andExpect(jsonPath("$.available").value(itemDto1.getAvailable()));
    }

    @Test
    void getItemByIdWrongIdItem() throws Exception {
        Mockito.when(itemService.getItemById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Вещь не найдена"));
        mockMvc.perform(get("/items/{itemId}", 100)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getItemByIdWrongIdUser() throws Exception {
        Mockito.when(itemService.getItemById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 100))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createItemEmptyDescription() throws Exception {
        Mockito.when(itemService.createItem(Mockito.anyLong(), Mockito.any()))
                .thenThrow(new ValidationException("Поле description не может быть пустым"));
        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getItemByOwnerWrongId() throws Exception {
        Mockito.when(itemService.getAllItemForOwner(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 100)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getItemByOwnerWrongSize() throws Exception {
        Mockito.when(itemService.getAllItemForOwner(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
                .thenThrow(new ValidationException("Параметры не корректны"));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "-1"))
                .andExpect(status().is4xxClientError());
    }
}