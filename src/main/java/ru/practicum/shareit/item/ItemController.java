package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.util.common.Marker;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @GetMapping
    public List<ItemDtoWithBookings> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/{id}")
    public ItemDtoWithBookings getItem(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") Long userId, @PathVariable long id) {
        return itemService.get(id, userId);
    }

    @PatchMapping("/{itemId}")
    @Validated({Marker.OnUpdate.class})
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable long itemId, @RequestBody @Valid ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    @Validated({Marker.OnCreate.class})
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable long itemId, @RequestBody @Valid CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
