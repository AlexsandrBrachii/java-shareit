package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужен молоток")
                .created(now)
                .items(new ArrayList<>())
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        Assertions.assertThat(result)
                .hasJsonPathNumberValue("$.id", 1)
                .hasJsonPathStringValue("$.created", now)
                .hasJsonPathStringValue("$.description", "Нужен молоток")
                .hasJsonPathArrayValue("$.items", new ArrayList<>());
    }

    @Test
    void testItemRequestDto_WithItem() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        ItemDto itemDto = ItemDto.builder()
                .id(2L)
                .name("Молоток")
                .available(true)
                .requestId(3L)
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужен молоток")
                .created(now)
                .items(List.of(itemDto))
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        Assertions.assertThat(result)
                .hasJsonPathNumberValue("$.id", 1)
                .hasJsonPathStringValue("$.created", now)
                .hasJsonPathStringValue("$.description", "Нужен молоток")
                .hasJsonPathArrayValue("$.items", List.of(itemDto))
                .hasJsonPathNumberValue("$.items[0].id", 2L)
                .hasJsonPathStringValue("$.items[0].name", "Молоток")
                .hasJsonPathBooleanValue("$.items[0].available", true)
                .hasJsonPathNumberValue("$.items[0].requestId", 3L);
    }
}
