package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;

@Slf4j
@Component
public class ItemStorageInMemory {

    private static int idCounter = 0;
    private Map<Integer, Item> items = new HashMap<>();

    public Item createItem(Integer idUser, Item item) {
        item.setId(++idCounter);
        item.setOwnerId(idUser);
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Integer idUser, Item item) {
        checkItemWithIdUser(idUser);

        Item itemCopy = null;
        for (Item item1 : items.values()) {
            if (Objects.equals(item1.getOwnerId(), idUser)) {
                itemCopy = item1;
            }
        }

        if (item.getAvailable() != null) {
            itemCopy.setAvailable(item.getAvailable());
        }
        if (item.getDescription() != null) {
            itemCopy.setDescription(item.getDescription());
        }
        if (item.getName() != null) {
            itemCopy.setName(item.getName());
        }

        items.put(itemCopy.getId(), itemCopy);

        return itemCopy;
    }

    public Item getItemById(Integer idUser, int idItem) {
        return items.get(idItem);
    }

    public Collection<Item> getAllItems(Integer idUser) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : items.values()) {
            if (Objects.equals(item.getOwnerId(), idUser)) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    public Collection<Item> findItems(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        String lowercaseText = text.toLowerCase();
        Collection<Item> itemCollection = new ArrayList<>();

        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(lowercaseText) || item.getDescription().toLowerCase().contains(lowercaseText)) && item.getAvailable() == true) {
                itemCollection.add(item);
            }
        }
        return itemCollection;
    }

    public void checkItemWithIdUser(Integer idUser) {
        boolean foundIdUser = false;
        for (Item item1 : items.values()) {
            if (Objects.equals(item1.getOwnerId(), idUser)) {
                foundIdUser = true;
                break;
            }
        }
        if (!foundIdUser) {
            throw new NotFoundException(String.format("У пользователя с id=%s не найдено вещей", idUser));
        }
    }
}
