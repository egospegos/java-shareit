package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    public BookingDto addNewBooking(long userId, BookingDto bookingDto) {
        validateItemId(bookingDto.getItemId());
        Item item = itemRepository.findById(bookingDto.getItemId());
        validateItem(item);
        validateUserId(userId);
        validateDateTime(bookingDto.getStart(), bookingDto.getEnd());
        if (item.getOwner().getId() == userId) {
            throw new DataNotFoundException("Владелец и арендатор один и тот же пользователь с id = " + userId);
        }

        //маппинг перед добавлением
        BookingMapper mapper = Mappers.getMapper(BookingMapper.class);
        Booking booking = mapper.bookingDtoToBooking(bookingDto);

        booking.setStatus(Status.WAITING);
        booking.setBooker(userRepository.findById(userId));
        booking.setItem(item);

        bookingRepository.save(booking);

        //маппинг перед отправкой на контролер
        bookingDto = mapper.bookingToBookingDto(booking);
        bookingDto.setItem(Mappers.getMapper(ItemMapper.class).itemToItemForBookingDto(booking.getItem()));
        bookingDto.setBooker(Mappers.getMapper(UserMapper.class).userToUserForBookingDto(booking.getBooker()));
        return bookingDto;
    }

    @Override
    public BookingDto setApproved(long userId, long bookingId, boolean approved) {
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
        if (approved) {
            booking.setStatus(Status.APPROVED);
        }
        if (!approved) {
            booking.setStatus(Status.REJECTED);
        }

        bookingRepository.save(booking);

        //маппинг перед отправкой на контролер
        BookingMapper mapper = Mappers.getMapper(BookingMapper.class);
        BookingDto bookingDto = mapper.bookingToBookingDto(booking);
        bookingDto.setItem(Mappers.getMapper(ItemMapper.class).itemToItemForBookingDto(booking.getItem()));
        bookingDto.setBooker(Mappers.getMapper(UserMapper.class).userToUserForBookingDto(booking.getBooker()));

        return bookingDto;
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        validateBookingId(bookingId);
        validateUserId(userId);
        Booking booking = bookingRepository.findById(bookingId);
        //проверка на автора бронирования или автора вещи
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new DataNotFoundException("Неверный id арендатора или владельца");
        }

        //маппинг
        BookingMapper mapper = Mappers.getMapper(BookingMapper.class);
        BookingDto bookingDto = mapper.bookingToBookingDto(booking);
        bookingDto.setItem(Mappers.getMapper(ItemMapper.class).itemToItemForBookingDto(booking.getItem()));
        bookingDto.setBooker(Mappers.getMapper(UserMapper.class).userToUserForBookingDto(booking.getBooker()));

        return bookingDto;
    }

    @Override
    public List<BookingDto> getAllByUserId(long userId, String state, Long from, Long size) {

        validateUserId(userId);
        validateState(state);

        List<Booking> bookings = new ArrayList<>();
        if (state.equals("ALL")) {
            long start = from / size;
            bookings = bookingRepository.findAllByBookerIdWithPagination(userId,
                    PageRequest.of(Math.toIntExact(start), Math.toIntExact(size), Sort.by("start_date").descending())).getContent();
        }
        if (state.equals("CURRENT")) {
            bookings = bookingRepository.findCurrentByBookerId(userId);
        }
        if (state.equals("PAST")) {
            bookings = bookingRepository.findPastByBookerId(userId);
        }
        if (state.equals("FUTURE")) {
            bookings = bookingRepository.findFutureByBookerId(userId);
        }
        if (state.equals("REJECTED")) {

            bookings = bookingRepository.findRejectedByBookerId(userId);
        }
        if (state.equals("WAITING")) {
            bookings = bookingRepository.findWaitingByBookerId(userId);
        }

        BookingMapper mapper = Mappers.getMapper(BookingMapper.class);
        List<BookingDto> bookingsDto = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingDto bookingDto = mapper.bookingToBookingDto(booking);
            bookingDto.setItem(Mappers.getMapper(ItemMapper.class).itemToItemForBookingDto(booking.getItem()));
            bookingDto.setBooker(Mappers.getMapper(UserMapper.class).userToUserForBookingDto(booking.getBooker()));
            bookingsDto.add(bookingDto);
        }

        return bookingsDto;


    }

    @Override
    public List<BookingDto> getAllByOwnerId(long ownerId, String state, Long from, Long size) {


        validateUserId(ownerId);
        validateState(state);

        List<Booking> bookings = new ArrayList<>();
        if (state.equals("ALL")) {
            long start = from / size;
            bookings = bookingRepository.findAllByOwnerIdWithPagination(ownerId,
                    PageRequest.of(Math.toIntExact(start), Math.toIntExact(size), Sort.by("start_date").descending())).getContent();
        }
        if (state.equals("CURRENT")) {
            bookings = bookingRepository.findCurrentByOwnerId(ownerId);
        }
        if (state.equals("PAST")) {
            bookings = bookingRepository.findPastByOwnerId(ownerId);
        }
        if (state.equals("FUTURE")) {
            bookings = bookingRepository.findFutureByOwnerId(ownerId);
        }
        if (state.equals("REJECTED")) {
            bookings = bookingRepository.findRejectedByOwnerId(ownerId);
        }
        if (state.equals("WAITING")) {
            bookings = bookingRepository.findWaitingByOwnerId(ownerId);
        }

        BookingMapper mapper = Mappers.getMapper(BookingMapper.class);
        List<BookingDto> bookingsDto = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingDto bookingDto = mapper.bookingToBookingDto(booking);
            bookingDto.setItem(Mappers.getMapper(ItemMapper.class).itemToItemForBookingDto(booking.getItem()));
            bookingDto.setBooker(Mappers.getMapper(UserMapper.class).userToUserForBookingDto(booking.getBooker()));
            bookingsDto.add(bookingDto);
        }

        return bookingsDto;


    }

    private void validateItem(Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("Параметр предмета available = false");
        }
    }

    private void validateItemId(Long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalStateException("Wrong item id=" + itemId));
    }

    private void validateUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Wrong user id=" + userId));
    }

    private void validateBookingId(Long bookingId) {
        bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalStateException("Wrong booking id=" + bookingId));
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
