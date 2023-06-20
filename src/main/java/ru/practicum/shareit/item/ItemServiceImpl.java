package ru.practicum.shareit.item;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
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
    public ItemDto get(long itemId) {
        validateId(itemId);
        //маппинг
        ItemMapper mapper = Mappers.getMapper(ItemMapper.class);
        return mapper.itemToItemDto(itemRepository.findById(itemId));
    }

    @Override
    public List<ItemDto> getAllByUserId(long userId) {
        List<Item> itemsFromRepo = itemRepository.findAllByUserId(userId);
        List<ItemDto> itemsDto = new ArrayList<>();
        //маппинг
        ItemMapper mapper = Mappers.getMapper(ItemMapper.class);
        for (Item item : itemsFromRepo) {
            itemsDto.add(mapper.itemToItemDto(item));
        }
        return itemsDto;
    }

    @Override
    public Item addNewItem(long userId, ItemDto itemDto) {
        //маппинг
        ItemMapper mapper = Mappers.getMapper(ItemMapper.class);
        Item item = mapper.itemDtoToItem(itemDto);
        item.setOwner(userRepository.findById(userId));
        return itemRepository.save(item);
    }

    @Override
    public Item update(long userId, long itemId, ItemDto itemDto) {
        //маппинг
        ItemMapper mapper = Mappers.getMapper(ItemMapper.class);
        Item item = mapper.itemDtoToItem(itemDto);
        return itemRepository.update(userId, itemId, item);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> itemsFromRepo = itemRepository.search(text); //поиск вещей
        //маппинг
        List<ItemDto> itemsDto = new ArrayList<>();
        ItemMapper mapper = Mappers.getMapper(ItemMapper.class);
        for (Item item : itemsFromRepo) {
            itemsDto.add(mapper.itemToItemDto(item));
        }
        return itemsDto;
    }

    private void validateId(Long id) {
        if (itemRepository.findById(id) == null || id < 0) {
            throw new DataNotFoundException("Предмет с таким id не найден");
        }
    }
}
