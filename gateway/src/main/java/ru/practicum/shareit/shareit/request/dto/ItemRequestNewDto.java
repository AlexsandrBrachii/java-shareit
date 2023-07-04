package ru.practicum.shareit.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestNewDto {

    @NotEmpty
    @Size(max = 255)
    private String description;

    @FutureOrPresent
    private LocalDateTime created;
}
