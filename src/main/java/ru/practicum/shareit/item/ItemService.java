package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(long userId, ItemDto itemDto);

    ItemDtoWithBookings get(long itemId, long userId);

    List<ItemDtoWithBookings> getAllByUserId(long userId);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
