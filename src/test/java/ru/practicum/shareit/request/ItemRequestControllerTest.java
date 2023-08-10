package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @InjectMocks
    private ItemRequestController itemRequestController;
    @Mock
    private ItemRequestService itemRequestService;

    @Test
    void add() {
        long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        when(itemRequestService.addNewItemRequest(userId, itemRequestDto)).thenReturn(itemRequestDto);

        ItemRequestDto actualRequest = itemRequestController.add(userId, itemRequestDto);
        assertEquals(itemRequestDto, actualRequest);
    }

    @Test
    void getAllByOwnerId() {
        long userId = 1L;
        List<ItemRequestDto> requestsDto = new ArrayList<>();

        when(itemRequestService.getAllByOwnerId(userId)).thenReturn(requestsDto);

        List<ItemRequestDto> actualList = itemRequestController.getAllByOwnerId(userId);
        assertEquals(requestsDto, actualList);
    }

    @Test
    void getAllWithPagination() {
        long userId = 1L;
        List<ItemRequestDto> requestsDto = new ArrayList<>();

        when(itemRequestService.getAllWithPagination(userId, null, null)).thenReturn(requestsDto);

        List<ItemRequestDto> actualList = itemRequestController.getAllWithPagination(userId, null, null);
        assertEquals(requestsDto, actualList);
    }

    @Test
    void getItemRequestById() {
        long userId = 1L;
        long requestId = 2L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        when(itemRequestService.getItemRequestById(requestId, userId)).thenReturn(itemRequestDto);

        ItemRequestDto actualRequest = itemRequestController.getItemRequestById(userId, requestId);
        assertEquals(itemRequestDto, actualRequest);
    }
}