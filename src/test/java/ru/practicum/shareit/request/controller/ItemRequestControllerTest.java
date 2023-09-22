package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.TestUtils.request;
import static ru.practicum.shareit.utils.Constants.USER_ID_HEADER;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private ResponseItemRequestDto responseItemRequestDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        responseItemRequestDto = ItemRequestMapper.toResponseItemRequestDto(request);
    }

    @Test
    void createItemRequestTest() throws Exception {
        when(itemRequestService.addItemRequest(any(), any(Long.class))).thenReturn(request);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.created", is(notNullValue())));
    }

    @Test
    void getItemRequestForUserTest() throws Exception {
        when(itemRequestService.getAllItemRequestsByOwner(any(Long.class))).thenReturn(List.of(responseItemRequestDto));
        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(responseItemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(notNullValue())));
    }

    @Test
    void getAllTest() throws Exception {
        when(itemRequestService.getAll(any(Long.class), any(Integer.class), any(Integer.class))).thenReturn(List.of(responseItemRequestDto));
        mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(responseItemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(notNullValue())));
    }

    @Test
    void getAllWithPaginationTest() throws Exception {
        when(itemRequestService.getAll(any(Long.class), any(Integer.class), any(Integer.class))).thenReturn(List.of(responseItemRequestDto));
        mvc.perform(get("/requests/all?from=0&size=10")
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(responseItemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(notNullValue())));
    }

    @Test
    void getByIdTest() throws Exception {
        when(itemRequestService.getById(any(Long.class), any(Long.class))).thenReturn(responseItemRequestDto);
        mvc.perform(get("/requests/1")
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(notNullValue())));
    }
}
