package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;


/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder(toBuilder = true)
public class ItemDto {

    @Setter
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

}
