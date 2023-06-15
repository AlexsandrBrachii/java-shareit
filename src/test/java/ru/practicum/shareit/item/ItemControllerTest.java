package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private Item item;
    private ItemDto addedItem;
    private ItemDto addedItem2;
    private ItemBookingDto itemBookingDto;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@example.com")
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .build();
        addedItem = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();

        addedItem2 = ItemDto.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .available(true)
                .build();

        itemBookingDto = ItemBookingDto.builder()
                .id(2L)
                .name("item")
                .description("item desc")
                .available(true)
                .comments(new ArrayList<>())
                .nextBooking(null)
                .lastBooking(null)
                .build();
    }

    @Test
    void createItem_Normal_return201() throws Exception {
        when(itemService.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(ItemMapper.itemToDto(item));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(addedItem))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void createItem_EmptyName_ReturnBadRequest() throws Exception {
        ItemDto wrong = addedItem.toBuilder().name(null).build();
        when(itemService.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(ItemMapper.itemToDto(item));

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(wrong))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createItem_EmptyDescription_ReturnBadRequest() throws Exception {
        ItemDto wrong = addedItem.toBuilder().description(null).build();
        when(itemService.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(ItemMapper.itemToDto(item));

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(wrong))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createItem_EmptyAvailable_ReturnBadRequest() throws Exception {
        ItemDto wrong = addedItem.toBuilder().available(null).build();
        when(itemService.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(ItemMapper.itemToDto(item));

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(wrong))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_Normal() throws Exception {
        when(itemService.updateItem(anyLong(), any(ItemDto.class)))
                .thenReturn(ItemMapper.itemToDto(item));

        mockMvc.perform(patch("/items/{id}", 1L)
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(addedItem))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void updateItem_UserIdIsNull_ReturnBadRequest() throws Exception {
        when(itemService.updateItem(anyLong(), any(ItemDto.class)))
                .thenReturn(ItemMapper.itemToDto(item));

        mockMvc.perform(patch("/items/{id}", 1L)
                .content(objectMapper.writeValueAsString(addedItem))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void getItemById_Normal() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemBookingDto);

        mockMvc.perform(get("/items/{id}", 1L)
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getAllItems_Normal() throws Exception {
        List<ItemBookingDto> items = List.of(itemBookingDto);
        when(itemService.getAllItems(1L))
                .thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value("item"))
                .andExpect(jsonPath("$[0].description").value("item desc"));

    }

    @Test
    void findItems_Normal() throws Exception {
        List<ItemDto> items = List.of(addedItem, addedItem2);
        when(itemService.findItems("name"))
                .thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "name")
                        .param("from", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].name").value("name2"));
    }

    @Test
    void findItems_EmptySearchText_ReturnEmptyList() throws Exception {

        when(itemService.findItems("name"))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/items/search")
                .param("text", "")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void createComment_Normal() throws Exception {

        CommentNewDto commentNewDto = CommentNewDto.builder()
                .text("first comment2")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("first comment")
                .authorName("user")
                .created(LocalDateTime.now())
                .build();

        when(itemService.addComment(anyLong(), anyLong(), any(CommentNewDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentNewDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }
}
