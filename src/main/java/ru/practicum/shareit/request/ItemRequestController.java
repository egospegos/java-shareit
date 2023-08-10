package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.util.common.Marker;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    //добавить новый запрос вещи
    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addNewItemRequest(userId, itemRequestDto);
    }

    // получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public List<ItemRequestDto> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllByOwnerId(userId);
    }

    //получить список запросов, созданных другими пользователями
    @GetMapping("/all")
    public List<ItemRequestDto> getAllWithPagination(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                     @RequestParam(required = false) Long from, @RequestParam(required = false) Long size) {
        return itemRequestService.getAllWithPagination(userId, from, size);
    }

    //получить данные об одном конкретном запросе вместе с данными об ответах
    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @PathVariable long requestId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }


}
