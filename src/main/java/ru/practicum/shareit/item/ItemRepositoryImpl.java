package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final HashMap<Long, Item> items = new HashMap();
    private long itemId = 1;

    @Override
    public Item findById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> findAllByUserId(long userId) {
        ArrayList<Item> itemArrayList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId() == userId) {
                itemArrayList.add(item);
            }
        }
        log.info("Количество пользователей в списке = {}", itemArrayList.size());
        return itemArrayList;
    }

    @Override
    public Item save(Item item) {
        item.setId(itemId++);
        items.put(item.getId(), item);
        log.info("Добавлен вещь с id = {}", item.getId());
        return item;
    }

    @Override
    public Item update(long userId, long itemId, Item item) {
        if (!items.containsKey(itemId)) {
            throw new ValidationException("Предмет по ключу с id: " + itemId + " не найден");
        }
        if (items.get(itemId).getOwner().getId() != userId) {
            throw new DataNotFoundException("id: " + itemId + " не совпадает с владельцем");
        }

        item.setId(itemId);
        if (item.getName() == null) {
            item.setName(items.get(itemId).getName());
            ; // присвоить старое значение, если новое не задано
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(itemId).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(items.get(itemId).getAvailable());
        }
        if (item.getOwner() == null) {
            item.setOwner(items.get(itemId).getOwner());
        }
        items.put(itemId, item);
        log.info("Изменён предмет с id = {}", item.getId());
        return item;
    }

    @Override
    public List<Item> search(String text) {
        ArrayList<Item> itemArrayList = new ArrayList<>();
        if (text.isEmpty()) {
            return itemArrayList;
        }
        text = text.toLowerCase();
        for (Item item : items.values()) {
            if (item.getAvailable() && (item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text))) {
                itemArrayList.add(item);
            }
        }
        return itemArrayList;
    }
}
