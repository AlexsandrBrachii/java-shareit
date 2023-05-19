package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */


@Data
public class ItemRequest {

    private Integer id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
