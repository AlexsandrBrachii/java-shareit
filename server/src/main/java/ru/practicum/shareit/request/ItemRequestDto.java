package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;

    private String description;

    private LocalDateTime created;

    @Setter
    private List<ItemDto> items = new ArrayList<>();
}
