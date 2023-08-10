package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addNewItemRequest(long userId, ItemRequestDto itemRequestDto) {
        //маппинг
        ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);
        ItemRequest itemRequest = mapper.itemRequestDtoToItemRequest(itemRequestDto);
        itemRequest.setRequester(userRepository.findById(userId));
        itemRequest.setCreated(LocalDateTime.now().withNano(0).plusSeconds(1));
        validateWithExceptions(itemRequest);

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

        for (ItemRequest request : requestsFromRepo) {
            ItemRequestDto requestDto = mapper.itemRequestToItemRequestDto(request);

            //проверка есть ли ответы(предметы) у запроса
            List<Item> items = itemRepository.findAllByRequestId(requestDto.getId());
            if (items.size() >= 1) {
                ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
                for (Item item : items) {
                    requestDto.getItems().add(itemMapper.itemToItemDtoWithRequestId(item));
                }
            }

            requestsDto.add(requestDto);
        }


        return requestsDto;
    }

    @Override
    public List<ItemRequestDto> getAllWithPagination(long userId, Long from, Long size) {
        //если параметры не передали
        if (from == null || size == null) {
            return new ArrayList<ItemRequestDto>();
        }
        validateFromSize(from, size);

        long start = from / size;
        List<ItemRequest> requests = itemRequestRepository.getAllWithPagination(userId,
                PageRequest.of(Math.toIntExact(start), Math.toIntExact(size), Sort.by("created").descending())).getContent();

        List<ItemRequestDto> requestsDto = new ArrayList<>();
        ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);
        for (ItemRequest request : requests) {
            ItemRequestDto requestDto = mapper.itemRequestToItemRequestDto(request);
            //проверка есть ли ответы(предметы) у запроса
            List<Item> items = itemRepository.findAllByRequestId(requestDto.getId());
            if (items.size() >= 1) {
                ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
                for (Item item : items) {
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


    private void validateWithExceptions(ItemRequest itemRequest) {
        if (itemRequest.getRequester() == null) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        if (itemRequest.getDescription().isEmpty()) {
            throw new ValidationException("Ошибка валидации нового запроса");
        }
    }

    private void validateRequestId(Long requestId) {
        if (!itemRequestRepository.findById(requestId).isPresent() || requestId < 0) {
            throw new DataNotFoundException("Предмет с таким id не найден");
        }
    }

    private void validateUserId(Long userId) {
        if (!userRepository.findById(userId).isPresent() || userId < 0) {
            throw new DataNotFoundException("Пользователь с таким id не найден");
        }
    }

    private void validateFromSize(Long from, Long size) {
        if (from == 0 && size == 0 || from < 0 || size < 0) {
            throw new ValidationException("Ошибка в параметрах from или size. from = " + from + ", size = " + size);
        }
    }
}
