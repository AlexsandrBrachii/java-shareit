package ru.practicum.shareit.item;


import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    ItemDto createItem(Long idUser, ItemDto item);

    ItemDto updateItem(Long idUser, ItemDto item);

    ItemBookingDto getItemById(Long idUser, Long idItem);

    Collection<ItemBookingDto> getAllItems(Long isUser, int from, int size);

    List<ItemDto> findItems(String text, int from, int size);

    CommentDto addComment(Long userId, Long itemId, CommentNewDto comment);

}
