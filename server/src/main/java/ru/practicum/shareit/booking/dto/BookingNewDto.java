package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Validated
public class BookingNewDto {

    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
