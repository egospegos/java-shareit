package ru.practicum.shareit.item;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody ItemDto itemDto) {
        ItemMapper mapper = Mappers.getMapper(ItemMapper.class);
        Item item = mapper.itemDtoToItem(itemDto);
        return itemService.addNewItem(userId, item);
    }

    @GetMapping
    public List<Item> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/{id}")
    public Item get(@PathVariable long id) {
        return itemService.get(id);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        ItemMapper mapper = Mappers.getMapper(ItemMapper.class);
        Item item = mapper.itemDtoToItem(itemDto);
        return itemService.update(userId, itemId, item);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
