package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class ItemStorageInMemory {

    private static int idCounter = 0;
    private Map<Integer, Item> items = new HashMap<>();

    public Item createItem(Item item) {
        item.setId(++idCounter);
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Integer idUser, Item item) {
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

    public Item getItemById(Integer idItem) {
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

    public Collection<Item> getItems() {
        return items.values();
    }
}
