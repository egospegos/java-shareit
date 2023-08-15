package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addNewItemRequest(long userId, ItemRequestDto itemRequestDto) {
        validateUserId(userId);
        //маппинг
        ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);
        ItemRequest itemRequest = mapper.itemRequestDtoToItemRequest(itemRequestDto);
        itemRequest.setRequester(userRepository.findById(userId));
        itemRequest.setCreated(LocalDateTime.now().withNano(0).plusSeconds(1));

        itemRequestRepository.save(itemRequest);

        //маппинг перед отправкой на контроллер
        return mapper.itemRequestToItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllByOwnerId(long ownerId) {

        validateUserId(ownerId);
        List<ItemRequest> requestsFromRepo = itemRequestRepository.findAllByOwnerId(ownerId);
        List<ItemRequestDto> requestsDto = new ArrayList<>();
        ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);
        ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

        List<Item> items = itemRepository.findAllWithRequestId();
        for (ItemRequest request : requestsFromRepo) {
            ItemRequestDto requestDto = mapper.itemRequestToItemRequestDto(request);

            //проверка есть ли ответы(предметы) у запроса
            for (Item item : items) {
                if (Objects.equals(request.getId(), item.getItemRequest().getId())) {
                    requestDto.getItems().add(itemMapper.itemToItemDtoWithRequestId(item));
                }
            }

            requestsDto.add(requestDto);
        }
        return requestsDto;
    }

    @Override
    public List<ItemRequestDto> getAllWithPagination(long userId, Long from, Long size) {

        long start = from / size;
        List<ItemRequest> requests = itemRequestRepository.getAllWithPagination(userId,
                PageRequest.of(Math.toIntExact(start), Math.toIntExact(size), Sort.by("created").descending())).getContent();

        List<ItemRequestDto> requestsDto = new ArrayList<>();
        ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);
        ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

        List<Item> items = itemRepository.findAllWithRequestId();
        for (ItemRequest request : requests) {
            ItemRequestDto requestDto = mapper.itemRequestToItemRequestDto(request);

            //проверка есть ли ответы(предметы) у запроса
            for (Item item : items) {
                if (Objects.equals(request.getId(), item.getItemRequest().getId())) {
                    requestDto.getItems().add(itemMapper.itemToItemDtoWithRequestId(item));
                }
            }
            requestsDto.add(requestDto);
        }

        return requestsDto;
    }

    @Override
    public ItemRequestDto getItemRequestById(long requestId, long userId) {
        validateRequestId(requestId);
        validateUserId(userId);

        ItemRequest request = itemRequestRepository.findById(requestId);
        ItemRequestMapper requestMapper = Mappers.getMapper(ItemRequestMapper.class);
        ItemRequestDto requestDto = requestMapper.itemRequestToItemRequestDto(request);
        //проверка есть ли ответы(предметы) у запроса
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        if (items.size() >= 1) {
            ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
            for (Item item : items) {
                requestDto.getItems().add(itemMapper.itemToItemDtoWithRequestId(item));
            }
        }

        return requestDto;
    }


    private void validateRequestId(Long requestId) {
        itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalStateException("Wrong itemRequest id=" + requestId));
    }

    private void validateUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Wrong user id=" + userId));
    }

}
