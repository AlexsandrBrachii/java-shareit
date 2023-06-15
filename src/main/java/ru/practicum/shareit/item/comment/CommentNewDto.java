package ru.practicum.shareit.item.comment;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentNewDto {
    private String text;
}
