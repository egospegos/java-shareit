package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Item get(long itemId) {
        validateId(itemId);
        return itemRepository.findById(itemId);
    }

    @Override
    public List<Item> getAllByUserId(long userId) {
        return itemRepository.findAllByUserId(userId);
    }

    @Override
    public Item addNewItem(long userId, Item item) {
        item.setOwner(userRepository.findById(userId));
        validateWithExceptions(item);
        return itemRepository.save(item);
    }

    @Override
    public Item update(long userId, long itemId, Item item) {
        return itemRepository.update(userId, itemId, item);
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemRepository.search(text);
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

    private void validateId(Long id) {
        if (itemRepository.findById(id) == null || id < 0) {
            throw new DataNotFoundException("Предмет с таким id не найден");
        }
    }
}
