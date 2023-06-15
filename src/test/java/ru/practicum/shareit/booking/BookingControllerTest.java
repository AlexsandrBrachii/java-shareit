package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.exception.ErrorMessage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({BookingController.class})
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;

    LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("молоток")
                .description("стальной молоток")
                .available(true)
                .build();

        UserDto booker = UserDto.builder()
                .id(2L)
                .name("booker")
                .email("booker@example.com")
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .start(now.plusDays(1).format(Utils.dtFormatter))
                .end(now.plusDays(2).format(Utils.dtFormatter))
                .item(itemDto)
                .booker(booker)
                .status("APPROVED")
                .build();
    }

    @Test
    void create_Normal() throws Exception {
        BookingNewDto bookingNewDto = BookingNewDto.builder()
                .itemId(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();
        when(bookingService.createBooking(anyLong(), any(BookingNewDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingNewDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }

    @Test
    void approve_Normal() throws Exception {
        when(bookingService.approve(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void approve_NoApprovedParam_ReturnBadRequest() throws Exception {
        when(bookingService.approve(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_Normal() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void getAll_Normal() throws Exception {
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        when(bookingService.getAllBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getAll_WrongFromSize_ReturnBadRequest() throws Exception {
        ErrorMessage message = new ErrorMessage("Не верный запрос для постраничного вывода.");

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from","0")
                        .param("size","0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(message)));
    }

    @Test
    void geOwnerAll_Normal() throws Exception {
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        when(bookingService.getAllBookingsForOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void geOwnerAll_WrongFromSize_ReturnBadRequest() throws Exception {
        ErrorMessage message = new ErrorMessage("Не верный запрос для постраничного вывода.");

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from","0")
                        .param("size","0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(message)));
    }
}
