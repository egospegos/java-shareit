package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.common.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.addRequest(userId, itemRequestDto);
    }

    // получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getAllByOwnerId(userId);
    }

    //получить список запросов, созданных другими пользователями
    @GetMapping("/all")
    public ResponseEntity<Object> getAllWithPagination(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                       @RequestParam(defaultValue = "10") @Positive Long size) {
        return itemRequestClient.getAllWithPagination(userId, from, size);
    }

    //получить данные об одном конкретном запросе вместе с данными об ответах
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                     @PathVariable long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
