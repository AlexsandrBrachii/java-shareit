package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item createItem(Integer idUser, ItemDto item);

    Item updateItem(Integer idUser, ItemDto item);

    Item getItemById(Integer idUser, int idItem);

    Collection<Item> getAllItems(Integer isUser);

    Collection<Item> findItems(String text);

}
