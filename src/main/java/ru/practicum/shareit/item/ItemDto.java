package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;


/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class ItemDto {

    private Integer id;
    private String name;
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean available;
    private Integer ownerId;
    private Integer requestId;
}