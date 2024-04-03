package ru.practicum.shareit.itemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.RequestServiceImpl;
import ru.practicum.shareit.request.dto.RequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTestMock {

    @MockBean
    RequestServiceImpl requestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final RequestDto itemRequestDto = new RequestDto();
    private final ItemDto itemDto = new ItemDto();
    private final List<ItemDto> itemDtos = new ArrayList<>();
    private final List<RequestDto> itemRequestDtos = new ArrayList<>();

    @BeforeEach
    void setUp() {
        itemDtos.add(itemDto);
        itemRequestDto.setId(1);
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setDescription("test");
        itemRequestDto.setUserId(1L);
        itemRequestDtos.add(itemRequestDto);
    }

    @Test
    void createItemRequest() throws Exception {
        Mockito.when(requestService.createRequest(Mockito.any(), Mockito.anyLong()))
                .thenReturn(itemRequestDto);
        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));
    }

    @Test
    void findAllItemRequestForRequestor() throws Exception {
        Mockito.when(requestService.getRequests(Mockito.anyLong()))
                .thenReturn(itemRequestDtos);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0]description").value(itemRequestDto.getDescription()));
    }

    @Test
    void findAllItemRequest() throws Exception {
        Mockito.when(requestService.getRequests(Mockito.anyLong()))
                .thenReturn(itemRequestDtos);
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void findItemRequestById() throws Exception {
        Mockito.when(requestService.getRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequestDto);
        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));
    }

    @Test
    void findItemRequestByIdWrongIdUser() throws Exception {
        Mockito.when(requestService.getRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 100))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findItemRequestByIdWrongIdRequest() throws Exception {
        Mockito.when(requestService.getRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Запрос не найден"));
        mockMvc.perform(get("/requests/{requestId}", 100)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }
}