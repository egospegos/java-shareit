package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private ItemRequestMapper requestMapper = Mappers.getMapper(ItemRequestMapper.class);

    @Test
    void addNewItemRequest() {
        long userId = 1;
        User user = new User();
        user.setId(userId);
        user.setName("name");
        user.setEmail("mail@mail.ru");

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("description");

        ItemRequest request = requestMapper.itemRequestDtoToItemRequest(requestDto);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now().withNano(0).plusSeconds(1));


        when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(user));
        when(userRepository.findById(userId)).thenReturn(user);
        when(itemRequestRepository.save(request)).thenReturn(request);

        ItemRequestDto actualRequest = itemRequestService.addNewItemRequest(userId, requestDto);


        Mockito.verify(itemRequestRepository).save(request);

    }


    @Test
    void getAllByOwnerId() {
        long ownerId = 1L;
        User owner = new User();
        owner.setId(ownerId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        long itemId = 3L;
        Item item = new Item();
        item.setId(itemId);
        item.setItemRequest(itemRequest);

        List<ItemRequest> ownerRequests = new ArrayList<>();
        ownerRequests.add(itemRequest);

        List<Item> itemsForRequest = new ArrayList<>();
        itemsForRequest.add(item);

        when(userRepository.findById(Long.valueOf(ownerId))).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findAllByOwnerId(ownerId)).thenReturn(ownerRequests);
        when(itemRepository.findAllWithRequestId()).thenReturn(itemsForRequest);

        List<ItemRequestDto> actualRequests = itemRequestService.getAllByOwnerId(ownerId);

        Mockito.verify(itemRequestRepository).findAllByOwnerId(ownerId);

    }

    @Test
    void getAllWithPagination() {
        long from = 4L;
        long size = 2L;
        long start = from / size;
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        long requestId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);

        long itemId = 3L;
        Item item = new Item();
        item.setId(itemId);
        item.setItemRequest(itemRequest);

        List<ItemRequest> ownerRequests = new ArrayList<>();
        ownerRequests.add(itemRequest);

        List<Item> itemsForRequest = new ArrayList<>();
        itemsForRequest.add(item);

        Page<ItemRequest> requestPage = new PageImpl<>(ownerRequests);

        when(itemRequestRepository.getAllWithPagination(userId, PageRequest.of(Math.toIntExact(start), Math.toIntExact(size), Sort.by("created").descending())))
                .thenReturn(requestPage);
        when(itemRepository.findAllWithRequestId()).thenReturn(itemsForRequest);

        List<ItemRequestDto> actualRequests = itemRequestService.getAllWithPagination(userId, from, size);

        Mockito.verify(itemRequestRepository).getAllWithPagination(userId, PageRequest.of(Math.toIntExact(start), Math.toIntExact(size), Sort.by("created").descending()));
    }


    @Test
    void getItemRequestById() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        long requestId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);

        long itemId = 3L;
        Item item = new Item();
        item.setId(itemId);
        item.setItemRequest(itemRequest);

        List<ItemRequest> ownerRequests = new ArrayList<>();
        ownerRequests.add(itemRequest);

        List<Item> itemsForRequest = new ArrayList<>();
        itemsForRequest.add(item);

        when(itemRequestRepository.findById(Long.valueOf(requestId))).thenReturn(Optional.of(itemRequest));
        when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(itemRequest);
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(itemsForRequest);

        ItemRequestDto actualRequests = itemRequestService.getItemRequestById(requestId, userId);

        Mockito.verify(itemRequestRepository).findById(requestId);
    }

    @Test
    void getItemRequestBy_whenIdInvalidRequestId() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        long requestId = -1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);

        assertThrows(DataNotFoundException.class,
                () -> itemRequestService.getItemRequestById(requestId, userId));

    }

    @Test
    void getItemRequestById_whenInvalidUserId() {
        long userId = -1L;
        User user = new User();
        user.setId(userId);

        long requestId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);

        when(itemRequestRepository.findById(Long.valueOf(requestId))).thenReturn(Optional.of(itemRequest));
        assertThrows(DataNotFoundException.class,
                () -> itemRequestService.getItemRequestById(requestId, userId));

    }
}