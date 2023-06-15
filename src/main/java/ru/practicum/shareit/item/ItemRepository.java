package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {

    Item save(Item item);
    Item findById(long itemId);
    List<Item> findAllByUserId(long userId);
    Item update(long userId, long itemId, Item item);
    List<Item> search(String text);
}
