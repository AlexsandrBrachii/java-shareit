package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;

/**
 * TODO Sprint add-bookings.
 */
@Builder
@Getter
@Setter
public class BookingDto {
    private Long id;
    private String start;
    private String end;
    private String status;
    private UserDto booker;
    private ItemDto item;
}
