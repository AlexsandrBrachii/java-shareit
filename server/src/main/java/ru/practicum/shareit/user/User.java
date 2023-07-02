package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;

    @NotBlank(message = "Имя не может быть пустым или отсутствовать.")
    @Column(name = "name_user")
    private String name;

    @NotBlank(message = "Email не может быть пустым или отсутствовать.")
    @Email(message = "Некорректный адрес электронной почты")
    @Column(unique = true)
    private String email;
}
