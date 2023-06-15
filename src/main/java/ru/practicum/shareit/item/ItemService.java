package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    Item addNewItem(long userId, Item item);
    Item get(long itemId);
    List<Item> getAllByUserId(long userId);
    Item update(long userId, long itemId, Item item);
    List<Item> searchItems(String text);
}
