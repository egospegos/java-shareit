package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public ItemDtoWithBookings get(long itemId, long userId) {
        validateItemId(itemId);

        Item item = itemRepository.findById(itemId);
        ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
        ItemDtoWithBookings itemDtoWithBookings = itemMapper.itemToItemDtoWithBookings(item);
        if (item.getOwner().getId() == userId) {

            //проверка есть ли бронирования у предмета
            List<Booking> bookings = bookingRepository.findAllByItemId(itemId);
            if (bookings.size() >= 1) {
                int size = bookings.size();
                BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

                if (size == 1) {
                    LocalDateTime start = bookings.get(0).getStart();
                    LocalDateTime end = bookings.get(0).getEnd();
                    LocalDateTime now = LocalDateTime.now();
                    if (start.isBefore(now) && end.isBefore(now) || start.isBefore(now) && (end.isAfter(now) || end.isEqual(now))) {
                        itemDtoWithBookings.setLastBooking(bookingMapper.bookingToBookingShort(bookings.get(0)));
                    }
                } else {
                    for (int i = size - 1; i > 0; i--) {
                        if (bookings.get(i).getEnd().isBefore(LocalDateTime.now())) {
                            itemDtoWithBookings.setLastBooking(bookingMapper.bookingToBookingShort(bookings.get(i)));
                            itemDtoWithBookings.setNextBooking(bookingMapper.bookingToBookingShort(bookings.get(i - 1)));
                        }

                    }
                }
            }
        }
        //проверка есть ли комментарии у предмета
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        if (comments.size() >= 1) {
            CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
            for (Comment comment : comments) {
                itemDtoWithBookings.getComments().add(commentMapper.commentToCommentDto(comment));
            }
        }
        return itemDtoWithBookings;

    }

    @Override
    public List<ItemDtoWithBookings> getAllByUserId(long userId) {
        List<Item> itemsFromRepo = itemRepository.findAllByUserId(userId);
        List<ItemDtoWithBookings> itemsDtoWithBookings = new ArrayList<>();
        ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

        for (Item item : itemsFromRepo) {
            ItemDtoWithBookings itemDtoWithBookings = mapper.itemToItemDtoWithBookings(item);
            List<Booking> bookings = bookingRepository.findAllByItemId(item.getId());
            if (bookings.size() >= 1) {
                int size = bookings.size();
                BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

                if (size == 1) {
                    LocalDateTime start = bookings.get(0).getStart();
                    LocalDateTime end = bookings.get(0).getEnd();
                    LocalDateTime now = LocalDateTime.now();
                    if (start.isBefore(now) && end.isBefore(now) || start.isBefore(now) && (end.isAfter(now) || end.isEqual(now))) {
                        itemDtoWithBookings.setLastBooking(bookingMapper.bookingToBookingShort(bookings.get(0)));

                    }
                } else {
                    for (int i = size - 1; i > 0; i--) {
                        if (bookings.get(i).getEnd().isBefore(LocalDateTime.now())) {
                            itemDtoWithBookings.setLastBooking(bookingMapper.bookingToBookingShort(bookings.get(i)));
                            itemDtoWithBookings.setNextBooking(bookingMapper.bookingToBookingShort(bookings.get(i - 1)));
                        }
                    }
                }
            }
            itemsDtoWithBookings.add(itemDtoWithBookings);
        }
        return itemsDtoWithBookings;
    }

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        //маппинг
        ItemMapper mapper = Mappers.getMapper(ItemMapper.class);
        Item item = mapper.itemDtoToItem(itemDto);
        item.setOwner(userRepository.findById(userId));
        validateWithExceptions(item);
        itemRepository.save(item);

        //маппинг перед отправкой на контроллер
        return mapper.itemToItemDto(item);

    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        validateItemId(itemId);
        validateUserId(itemId, userId);
        //маппинг
        ItemMapper mapper = Mappers.getMapper(ItemMapper.class);
        Item item = mapper.itemDtoToItem(itemDto);

        item.setId(itemId);
        item.setOwner(userRepository.findById(userId));
        if (item.getName() == null) {
            item.setName(itemRepository.findById(itemId).getName()); // присвоить старое значение, если новое не задано
        }
        if (item.getDescription() == null) {
            item.setDescription(itemRepository.findById(itemId).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(itemRepository.findById(itemId).getAvailable());
        }
        if (item.getOwner() == null) {
            item.setOwner(itemRepository.findById(itemId).getOwner());
        }
        itemRepository.save(item);
        //маппинг перед отправкой на контроллер
        return mapper.itemToItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemsFromRepo = itemRepository.search(text); //поиск вещей
        //маппинг
        List<ItemDto> itemsDto = new ArrayList<>();
        ItemMapper mapper = Mappers.getMapper(ItemMapper.class);
        for (Item item : itemsFromRepo) {
            itemsDto.add(mapper.itemToItemDto(item));
        }
        return itemsDto;
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {

        //есть ли такие бронирования, чтоб коммент оставить
        List<Booking> bookings = bookingRepository.findByItemIdAndUserId(itemId, userId);
        if (bookings.size() == 0) {
            throw new ValidationException("Нет бронирования по item_id=" + itemId + " и user_id=" + userId);
        }
        //бронирования не в будущем?
        boolean isBookingInFuture = true;
        for (Booking booking : bookings) {
            if (booking.getEnd().isBefore(LocalDateTime.now())) {
                isBookingInFuture = false;
            }
        }
        if (isBookingInFuture) {
            throw new ValidationException("Бронирование в будущем");
        }
        //маппинг
        CommentMapper mapper = Mappers.getMapper(CommentMapper.class);
        Comment comment = mapper.commentDtoToComment(commentDto);
        comment.setItem(itemRepository.findById(itemId));
        comment.setAuthor(userRepository.findById(userId));
        comment.setCreated(LocalDateTime.now());

        //маппинг перед отправкой назад
        return mapper.commentToCommentDto(commentRepository.save(comment));

    }

    private void validateItemId(Long itemId) {
        if (!itemRepository.findById(itemId).isPresent() || itemId < 0) {
            throw new DataNotFoundException("Предмет с таким id не найден");
        }
    }

    private void validateUserId(long itemId, long userId) {
        if (itemRepository.findById(itemId).getOwner().getId() != userId) {
            throw new DataNotFoundException("id пользователя: " + userId + " не совпадает с владельцем");
        }
    }

    private void validateWithExceptions(Item item) {
        if (item.getOwner() == null) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        if (item.getAvailable() == null || item.getName() == null || item.getDescription() == null) {
            throw new ValidationException("Ошибка валидации нового предмета");
        }
        if (item.getName().isEmpty() || item.getDescription().isEmpty()) {
            throw new ValidationException("Ошибка валидации нового предмета");
        }
    }

}
