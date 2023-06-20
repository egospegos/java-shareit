package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap();
    private long itemId = 1;

    @Override
    public Item findById(long itemId) {

        return items.get(itemId);
    }

    @Override
    public List<Item> findAllByUserId(long userId) {
        List<Item> itemArrayList = new ArrayList<>();
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
        validateWithExceptions(item);
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
            item.setName(items.get(itemId).getName()); // присвоить старое значение, если новое не задано
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
        //если сообщение для поиска пустое, то вернуть пустой список
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return items.values().stream()
                .filter(item -> isSearched(item, text.toLowerCase(Locale.ENGLISH)))
                .collect(Collectors.toList());
    }

    private boolean isSearched(Item item, String text) {
        boolean available = item.getAvailable();
        String name = item.getName().toLowerCase();
        String description = item.getDescription().toLowerCase();
        return available && (name.contains(text) || description.contains(text));
    }

    private void validateWithExceptions(Item item) {
        if (item.getOwner() == null) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        if (item.getAvailable() == null || item.getName() == null || item.getDescription() == null) {
            throw new ValidationException("Ошибка валидации нового предмета");
        }
        if (item.getName().isEmpty() || item.getDescription().isEmpty()) {
            throw new ValidationException("Ошибка валидации нового предмета");
        }
    }
}
