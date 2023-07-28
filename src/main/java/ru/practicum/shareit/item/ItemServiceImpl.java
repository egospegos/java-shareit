package ru.practicum.shareit.item;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

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
    public Item addNewItem(long userId, ItemDto itemDto) {
        //маппинг
        ItemMapper mapper = Mappers.getMapper(ItemMapper.class);
        Item item = mapper.itemDtoToItem(itemDto);
        item.setOwner(userRepository.findById(userId));
        validateWithExceptions(item);
        return itemRepository.save(item);
    }

    @Override
    public Item update(long userId, long itemId, ItemDto itemDto) {
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
        return itemRepository.save(item);
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
    public CommentDto addComment(long userId, long itemId, Comment comment) {

        validateComment(comment);
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
        comment.setItem(itemRepository.findById(itemId));
        comment.setAuthor(userRepository.findById(userId));
        comment.setCreated(LocalDateTime.now());

        CommentMapper mapper = Mappers.getMapper(CommentMapper.class);
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

    private void validateComment(Comment comment) {
        if (comment.getText().isEmpty()) {
            throw new ValidationException("Ошибка валидации комментарий пустой");
        }
    }
}
