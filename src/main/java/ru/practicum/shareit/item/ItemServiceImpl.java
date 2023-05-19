package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collection;


@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorageInMemory itemStorage;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(Integer idUser, ItemDto itemDto) {
        if (idUser == null) {
            throw new RuntimeException("id пользователя не может быть пустым");
        }
        userService.getUserById(idUser);
        validateItem(itemDto);
        Item item = itemStorage.createItem(idUser, itemMapper.dtoToObject(itemDto));
        return itemMapper.objectToDto(item);
    }

    @Override
    public ItemDto updateItem(Integer idUser, ItemDto itemDto) {
        if (idUser == null) {
            throw new RuntimeException("id пользователя не может быть пустым");
        }
        Item item = itemStorage.updateItem(idUser, itemMapper.dtoToObject(itemDto));
        return itemMapper.objectToDto(item);
    }

    @Override
    public ItemDto getItemById(Integer idUser, Integer idItem) {
        return itemMapper.objectToDto(itemStorage.getItemById(idUser, idItem));
    }

    @Override
    public Collection<ItemDto> getAllItems(Integer idUser) {
        Collection<Item> items = itemStorage.getAllItems(idUser);
        Collection<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemMapper.objectToDto(item));
        }
        return itemsDto;
    }

    @Override
    public Collection<ItemDto> findItems(String text) {
        Collection<Item> items = itemStorage.findItems(text);
        Collection<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemMapper.objectToDto(item));
        }
        return itemsDto;
    }

    public void validateItem(ItemDto itemDto) {
        if (itemDto.getAvailable() == null || itemDto.getName().isEmpty() || itemDto.getDescription() == null) {
            throw new ValidationException("Некоторые поля Item пусты");
        }
    }
}
