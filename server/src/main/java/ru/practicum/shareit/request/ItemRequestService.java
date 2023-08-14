package ru.practicum.shareit.request;


import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addNewItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllByOwnerId(long userId);

    List<ItemRequestDto> getAllWithPagination(long userId, Long from, Long size);

    ItemRequestDto getItemRequestById(long requestId, long userId);


}
