package ru.practicum.shareit.booking;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Booking addNewBooking(long userId, BookingDto bookingDto) {
        validateItemId(bookingDto.getItemId());
        Item item = itemRepository.findById(bookingDto.getItemId());
        validateItem(item);
        validateUserId(userId);
        validateDateTime(bookingDto.getStart(), bookingDto.getEnd());
        if (item.getOwner().getId() == userId) {
            throw new DataNotFoundException("Владелец и арендатор один и тот же пользователь с id = " + userId);
        }

        BookingMapper mapper = Mappers.getMapper(BookingMapper.class);
        Booking booking = mapper.bookingDtoToBooking(bookingDto);

        booking.setStatus(Status.WAITING);
        booking.setBooker(userRepository.findById(userId));
        booking.setItem(item);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking setApproved(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking.getBooker().getId() == userId) {
            throw new DataNotFoundException("Арендатор не может сменить статус сменить статус");
        }
        //проверка, что это владелец вещи
        if (booking.getItem().getOwner().getId() != userId) {
            throw new ValidationException("Пользователь не является владельцем вещи");
        }
        if (booking.getStatus().equals(Status.APPROVED) && approved == true) {
            throw new ValidationException("Статус APPROVED уже назначен");
        }
        if (approved == true) {
            booking.setStatus(Status.APPROVED);
        }
        if (approved == false) {
            booking.setStatus(Status.REJECTED);
        }

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(long userId, long bookingId) {
        validateBookingId(bookingId);
        validateUserId(userId);
        Booking booking = bookingRepository.findById(bookingId);
        //проверка на автора бронирования или автора вещи
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new DataNotFoundException("Неверный id арендатора или владельца");
        }
        return booking;
    }

    @Override
    public List<Booking> getAllByUserId(long userId, String state) {

        validateUserId(userId);
        validateState(state);
        if (state.equals("ALL")) {
            return bookingRepository.findAllByBookerId(userId);
        }
        if (state.equals("CURRENT")) {
            return bookingRepository.findCurrentByBookerId(userId);
        }
        if (state.equals("PAST")) {

            return bookingRepository.findPastByBookerId(userId);
        }
        if (state.equals("FUTURE")) {
            return bookingRepository.findFutureByBookerId(userId);
        }
        if (state.equals("REJECTED")) {

            return bookingRepository.findRejectedByBookerId(userId);
        }
        if (state.equals("WAITING")) {

            return bookingRepository.findWaitingByBookerId(userId);
        }

        return null;
    }

    @Override
    public List<Booking> getAllByOwnerId(long ownerId, String state) {

        validateUserId(ownerId);
        validateState(state);
        if (state.equals("ALL")) {
            return bookingRepository.findAllByOwnerId(ownerId);
        }
        if (state.equals("CURRENT")) {
            return bookingRepository.findCurrentByOwnerId(ownerId);
        }
        if (state.equals("PAST")) {
            return bookingRepository.findPastByOwnerId(ownerId);
        }
        if (state.equals("FUTURE")) {
            return bookingRepository.findFutureByOwnerId(ownerId);
        }
        if (state.equals("REJECTED")) {

            return bookingRepository.findRejectedByOwnerId(ownerId);
        }
        if (state.equals("WAITING")) {
            return bookingRepository.findWaitingByOwnerId(ownerId);
        }

        return null;
    }

    private void validateItem(Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("Параметр предмета available = false");
        }
    }

    private void validateItemId(Long itemId) {
        if (!itemRepository.findById(itemId).isPresent() || itemId < 0) {
            throw new DataNotFoundException("Предмет с таким id не найден");
        }
    }

    private void validateUserId(Long userId) {
        if (!userRepository.findById(userId).isPresent() || userId < 0) {
            throw new DataNotFoundException("Пользователь с таким id не найден");
        }
    }

    private void validateBookingId(Long bookingId) {
        if (!bookingRepository.findById(bookingId).isPresent() || bookingId < 0) {
            throw new DataNotFoundException("Бронирование с таким id не найдено");
        }
    }

    private void validateDateTime(LocalDateTime start, LocalDateTime end) {
        if (end == null || start == null) {
            throw new ValidationException("Одна из дат равна null");
        }
        if (end.isBefore(LocalDateTime.now()) || end.isBefore(start) || end.isEqual(start) ||
                start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Даты заданы неправильно");
        }
    }

    private void validateState(String state) {
        if (!state.equals("ALL") && !state.equals("CURRENT") && !state.equals("PAST") && !state.equals("FUTURE")
                && !state.equals("WAITING") && !state.equals("REJECTED")) {
            throw new ValidationException("Unknown state: " + state);
        }
    }
}
