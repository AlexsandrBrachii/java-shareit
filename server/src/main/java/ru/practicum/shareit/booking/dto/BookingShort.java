package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingShort {
    private Long id;
    private Long bookerId;

    // Lombok AllArgsConstructor не обрабатывает c JPQL
    public BookingShort(Long id, Long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}
