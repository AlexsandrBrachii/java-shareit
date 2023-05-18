package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorageInMemory;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorageInMemory itemStorage;
    private final UserService userService;

    @Override
    public Item createItem(Integer idUser, ItemDto item) {
        if (idUser == null) {
            throw new RuntimeException("id пользователя не может быть пустым");
        }
        if (userService.getUser(idUser) == null) {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", idUser));
        }
        validateItem(item);
        return itemStorage.createItem(idUser, item);
    }

    @Override
    public Item updateItem(Integer idUser, ItemDto item) {
        if (idUser == null) {
            throw new RuntimeException("id пользователя не может быть пустым");
        }
        return itemStorage.updateItem(idUser, item);
    }

    @Override
    public Item getItemById(Integer idUser, int idItem) {
        return itemStorage.getItemById(idUser, idItem);
    }

    @Override
    public Collection<Item> getAllItems(Integer isUser) {
        return itemStorage.getAllItems(isUser);
    }

    @Override
    public Collection<Item> findItems(String text) {
        return itemStorage.findItems(text);
    }

    public void validateItem(ItemDto item) {
        if (item.getAvailable() == null || item.getName().isEmpty() || item.getDescription() == null) {
            throw new ValidationException("Некоторые поля Item пусты");
        }
    }
}
