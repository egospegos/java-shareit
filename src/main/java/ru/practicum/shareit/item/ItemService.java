package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    Item addNewItem(long userId, ItemDto itemDto);

    ItemDto get(long itemId);

    List<ItemDto> getAllByUserId(long userId);

    Item update(long userId, long itemId, ItemDto itemDto);

    List<ItemDto> searchItems(String text);
}
