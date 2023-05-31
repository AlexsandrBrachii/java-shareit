package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "Имя не может быть пустым или отсутствовать.")
    @Size(max = 255, message = "Имя не должна быть больше 50 символов.")
    private String name;

    @NotBlank(message = "Email не может быть пустым или отсутствовать.")
    @Email(message = "Некорректный адрес электронной почты")
    @Size(max = 512, message = "Email не должна быть больше 512 символов.")
    private String email;
}
