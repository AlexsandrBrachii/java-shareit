package ru.practicum.shareit.item;


import java.util.Collection;

public interface ItemService {

    ItemDto createItem(Integer idUser, ItemDto item);

    ItemDto updateItem(Integer idUser, ItemDto item);

    ItemDto getItemById(Integer idUser, Integer idItem);

    Collection<ItemDto> getAllItems(Integer isUser);

    Collection<ItemDto> findItems(String text);

}
