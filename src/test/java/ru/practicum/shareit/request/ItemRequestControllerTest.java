package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.data.Constants.TITLE_ITEM_BOOKING;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    RequestServiceImpl requestService;

    @Test
    void testCreateItemRequest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Description");

        RequestDto responseDto = new RequestDto();
        responseDto.setId(1L);

        when(requestService.createRequest(any(ItemRequestDto.class), anyLong())).thenReturn(responseDto);

        mvc.perform(post("/requests")
                        .header(TITLE_ITEM_BOOKING, "1")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class));
    }

    @Test
    void testCreateItemFailDescription() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("");
        mvc.perform(post("/requests")
                        .header(TITLE_ITEM_BOOKING, "1")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindByAuthor() throws Exception {
        RequestDto responseDto = new RequestDto();
        responseDto.setId(1L);

        when(requestService.getRequests(anyLong())).thenReturn(List.of(responseDto));
        mvc.perform(get("/requests")
                        .header(TITLE_ITEM_BOOKING, "1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(responseDto.getId()), Long.class));
    }

    @Test
    void testFindAll() throws Exception {
        RequestDto responseDto = new RequestDto();
        responseDto.setId(1L);

        when(requestService.getCurrentCountOfRequests(anyInt(), anyInt(), anyLong())).thenReturn(List.of(responseDto));

        mvc.perform(get("/requests/all")
                        .header(TITLE_ITEM_BOOKING, "1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(responseDto.getId()), Long.class));
    }

    @Test
    void testFindById() throws Exception {
        RequestDto responseDto = new RequestDto();
        responseDto.setId(1L);

        when(requestService.getRequestById(anyLong(), anyLong())).thenReturn(responseDto);

        mvc.perform(get("/requests/1")
                        .header(TITLE_ITEM_BOOKING, "1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class));
    }
}