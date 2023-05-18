package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.toItem;

@Component
public class ItemStorageInMemory {

    private static int idCounter = 0;
    Map<Integer, List<Item>> items = new HashMap<>();

    public Item createItem(int idUser, ItemDto item) {
        Item itemResult = toItem(item);
        itemResult.setId(++idCounter);
        itemResult.setOwnerId(idUser);
        List<Item> itemList = new ArrayList<>();
        itemList.add(itemResult);
        items.put(idUser, itemList);
        return itemResult;
    }

    public Item updateItem(Integer idUser, ItemDto item) {
        if (!items.containsKey(idUser)) {
            throw new NotFoundException(String.format("У пользователя с id=%s не найдено вещей", idUser));
        }

        List<Item> itemList = items.get(idUser);
        Item itemCopy = itemList.get(0);

        if (item.getAvailable() != null) {
            itemCopy.setAvailable(item.getAvailable());
        }
        if (item.getDescription() != null) {
            itemCopy.setDescription(item.getDescription());
        }
        if (item.getName() != null) {
            itemCopy.setName(item.getName());
        }

        items.put(idUser, Collections.singletonList(itemCopy));

        return itemCopy;
    }

    public Item getItemById(Integer idUser, int idItem) {
        Item itemResult = null;
        for (List<Item> itemList : items.values()) {
            for (Item item : itemList) {
                if (item.getId() == idItem) {
                    itemResult = item;
                    break;
                }
            }
            if (itemResult != null) {
                break;
            }
        }
        return itemResult;
    }

    public Collection<Item> getAllItems(Integer idUser) {
        return items.get(idUser);
    }

    public Collection<Item> findItems(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        String lowercaseText = text.toLowerCase();

        return items.values().stream()
                .flatMap(List::stream)
                .filter(item -> item.getName().toLowerCase().contains(lowercaseText) ||
                        item.getDescription().toLowerCase().contains(lowercaseText))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
