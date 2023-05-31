package ru.practicum.shareit.item;


import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    ItemDto createItem(Long idUser, ItemDto item);

    ItemDto updateItem(Long idUser, ItemDto item);

    ItemBookingDto getItemById(Long idUser, Long idItem);

    Collection<ItemBookingDto> getAllItems(Long isUser);

    List<ItemDto> findItems(String text);

    CommentDto addComment(Long userId, Long itemId, CommentNewDto comment);

}
