package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void addNewBooking() {
        long userId1 = 1L;
        User user1 = new User();
        user1.setId(userId1);

        long userId2 = 2L;
        User user2 = new User();
        user2.setId(userId2);

        long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(user2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        Booking booking = bookingMapper.bookingDtoToBooking(bookingDto);
        booking.setStatus(Status.WAITING);
        booking.setBooker(user1);
        booking.setItem(item);

        BookingDto expectedBooking = bookingMapper.bookingToBookingDto(booking);
        expectedBooking.setItem(itemMapper.itemToItemForBookingDto(item));
        expectedBooking.setBooker(userMapper.userToUserForBookingDto(user1));


        Mockito.when(itemRepository.findById(Long.valueOf(itemId))).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(item);
        Mockito.when(userRepository.findById(Long.valueOf(userId1))).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.findById(userId1)).thenReturn(user1);
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto actualBooking = bookingService.addNewBooking(userId1, bookingDto);

        Mockito.verify(bookingRepository).save(booking);
    }

    @Test
    void addNewBooking_withInvalidItemId() {
        long userId1 = 1L;
        User user1 = new User();
        user1.setId(userId1);

        long userId2 = 2L;
        User user2 = new User();
        user2.setId(userId2);

        long itemId = -1L;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(false);
        item.setOwner(user2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);

        assertThrows(DataNotFoundException.class,
                () -> bookingService.addNewBooking(userId1, bookingDto));
    }

    @Test
    void addNewBooking_withInvalidItem() {
        long userId1 = 1L;
        User user1 = new User();
        user1.setId(userId1);

        long userId2 = 2L;
        User user2 = new User();
        user2.setId(userId2);

        long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(false);
        item.setOwner(user2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);

        Mockito.when(itemRepository.findById(Long.valueOf(itemId))).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(item);

        assertThrows(ValidationException.class,
                () -> bookingService.addNewBooking(userId1, bookingDto));
    }

    @Test
    void addNewBooking_withInvalidUserId() {
        long userId1 = -1L;
        User user1 = new User();
        user1.setId(userId1);

        long userId2 = 2L;
        User user2 = new User();
        user2.setId(userId2);

        long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(user2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);

        Mockito.when(itemRepository.findById(Long.valueOf(itemId))).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(item);

        assertThrows(DataNotFoundException.class,
                () -> bookingService.addNewBooking(userId1, bookingDto));
    }

    @Test
    void addNewBooking_whenOwnerAndUserTheSame() {
        long userId1 = 2L;
        User user1 = new User();
        user1.setId(userId1);

        long userId2 = 2L;
        User user2 = new User();
        user2.setId(userId2);

        long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(user2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));


        Mockito.when(itemRepository.findById(Long.valueOf(itemId))).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(item);
        Mockito.when(userRepository.findById(Long.valueOf(userId1))).thenReturn(Optional.of(user1));
        ;

        assertThrows(DataNotFoundException.class,
                () -> bookingService.addNewBooking(userId1, bookingDto));


    }

    @Test
    void setApproved_ifApprovedTrue() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        User user1 = new User();
        user1.setId(userId);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setOwner(user1);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(booking);
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto expectedBooking = bookingMapper.bookingToBookingDto(booking);
        expectedBooking.setItem(itemMapper.itemToItemForBookingDto(booking.getItem()));
        expectedBooking.setBooker(userMapper.userToUserForBookingDto(booking.getBooker()));

        BookingDto actualBooking = bookingService.setApproved(userId, bookingId, approved);

        Mockito.verify(bookingRepository).save(booking);
    }

    @Test
    void setApproved_ifApprovedFalse() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = false;

        User user1 = new User();
        user1.setId(userId);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setOwner(user1);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(booking);
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto expectedBooking = bookingMapper.bookingToBookingDto(booking);
        expectedBooking.setItem(itemMapper.itemToItemForBookingDto(booking.getItem()));
        expectedBooking.setBooker(userMapper.userToUserForBookingDto(booking.getBooker()));

        BookingDto actualBooking = bookingService.setApproved(userId, bookingId, approved);

        Mockito.verify(bookingRepository).save(booking);
    }

    @Test
    void setApproved_InvalidBookerId() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        User user1 = new User();
        user1.setId(userId);

        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setOwner(user1);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(booking);

        assertThrows(DataNotFoundException.class,
                () -> bookingService.setApproved(userId, bookingId, approved));
    }

    @Test
    void setApproved_InvalidOwnerId() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        User user1 = new User();
        user1.setId(3L);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setOwner(user1);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(booking);

        assertThrows(ValidationException.class,
                () -> bookingService.setApproved(userId, bookingId, approved));
    }

    @Test
    void setApproved_alreadyApproved() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        User user1 = new User();
        user1.setId(userId);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setOwner(user1);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(booking);
        assertThrows(ValidationException.class,
                () -> bookingService.setApproved(userId, bookingId, approved));
    }

    @Test
    void getBooking() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);

        long bookingId = 1L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(Long.valueOf(bookingId))).thenReturn(Optional.of(booking));
        Mockito.when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(booking);

        BookingDto actualBooking = bookingService.getBooking(userId, bookingId);
        Mockito.verify(bookingRepository).findById(bookingId);

    }

    @Test
    void getBooking_whenNotAuthor() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        User notOwner = new User();
        notOwner.setId(2L);

        long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(notOwner);

        long bookingId = 1L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(notOwner);

        Mockito.when(bookingRepository.findById(Long.valueOf(bookingId))).thenReturn(Optional.of(booking));
        Mockito.when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(booking);

        assertThrows(DataNotFoundException.class,
                () -> bookingService.getBooking(userId, bookingId));

    }

    @Test
    void getBooking_whenInvalidBookingId() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);

        long bookingId = -1L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(Long.valueOf(bookingId))).thenReturn(Optional.of(booking));

        assertThrows(DataNotFoundException.class,
                () -> bookingService.getBooking(userId, bookingId));

    }

    @Test
    void getAllByUserId_WithoutPagination_StateAll() {
        long userId = 1L;
        String state = "ALL";
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());

        Mockito.when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(new User()));
        Mockito.when(bookingRepository.findAllByBookerId(userId)).thenReturn(bookings);

        List<BookingDto> actualList = bookingService.getAllByUserId(userId, state, null, null);

        Mockito.verify(bookingRepository).findAllByBookerId(userId);

    }

    @Test
    void getAllByUserId_WithoutPagination_StateCurrent() {
        long userId = 1L;
        String state = "CURRENT";
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());

        Mockito.when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(new User()));
        Mockito.when(bookingRepository.findCurrentByBookerId(userId)).thenReturn(bookings);

        List<BookingDto> actualList = bookingService.getAllByUserId(userId, state, null, null);

        Mockito.verify(bookingRepository).findCurrentByBookerId(userId);

    }

    @Test
    void getAllByUserId_WithoutPagination_StatePast() {
        long userId = 1L;
        String state = "PAST";
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());

        Mockito.when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(new User()));
        Mockito.when(bookingRepository.findPastByBookerId(userId)).thenReturn(bookings);

        List<BookingDto> actualList = bookingService.getAllByUserId(userId, state, null, null);

        Mockito.verify(bookingRepository).findPastByBookerId(userId);

    }

    @Test
    void getAllByUserId_WithoutPagination_StateFuture() {
        long userId = 1L;
        String state = "FUTURE";
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());

        Mockito.when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(new User()));
        Mockito.when(bookingRepository.findFutureByBookerId(userId)).thenReturn(bookings);

        List<BookingDto> actualList = bookingService.getAllByUserId(userId, state, null, null);

        Mockito.verify(bookingRepository).findFutureByBookerId(userId);
    }

    @Test
    void getAllByUserId_WithoutPagination_StateRejected() {
        long userId = 1L;
        String state = "REJECTED";
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());

        Mockito.when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(new User()));
        Mockito.when(bookingRepository.findRejectedByBookerId(userId)).thenReturn(bookings);

        List<BookingDto> actualList = bookingService.getAllByUserId(userId, state, null, null);

        Mockito.verify(bookingRepository).findRejectedByBookerId(userId);
    }

    @Test
    void getAllByUserId_WithoutPagination_StateWaiting() {
        long userId = 1L;
        String state = "WAITING";
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());

        Mockito.when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(new User()));
        Mockito.when(bookingRepository.findWaitingByBookerId(userId)).thenReturn(bookings);

        List<BookingDto> actualList = bookingService.getAllByUserId(userId, state, null, null);

        Mockito.verify(bookingRepository).findWaitingByBookerId(userId);
    }

    @Test
    void getAllByUserId_WithPagination() {
        long userId = 1L;
        String state = "ALL";
        long from = 4L;
        long size = 2L;
        long start = from / size;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        Page<Booking> bookingPage = new PageImpl<>(bookings);

        Mockito.when(bookingRepository.findAllByBookerIdWithPagination(userId, PageRequest.of(Math.toIntExact(start), Math.toIntExact(size), Sort.by("start_date").descending())))
                .thenReturn(bookingPage);

        List<BookingDto> actualList = bookingService.getAllByUserId(userId, state, from, size);

        Mockito.verify(bookingRepository).findAllByBookerIdWithPagination(userId, PageRequest.of(Math.toIntExact(start), Math.toIntExact(size), Sort.by("start_date").descending()));

    }

    @Test
    void getAllByOwnerId() {
    }

    @Test
    void getAllByOwnerId_WithoutPagination_StateAll() {
        long userId = 1L;
        String state = "ALL";
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());

        Mockito.when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(new User()));
        Mockito.when(bookingRepository.findAllByOwnerId(userId)).thenReturn(bookings);

        List<BookingDto> actualList = bookingService.getAllByOwnerId(userId, state, null, null);

        Mockito.verify(bookingRepository).findAllByOwnerId(userId);

    }

    @Test
    void getAllByOwnerId_WithoutPagination_StateCurrent() {
        long userId = 1L;
        String state = "CURRENT";
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());

        Mockito.when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(new User()));
        Mockito.when(bookingRepository.findCurrentByOwnerId(userId)).thenReturn(bookings);

        List<BookingDto> actualList = bookingService.getAllByOwnerId(userId, state, null, null);

        Mockito.verify(bookingRepository).findCurrentByOwnerId(userId);

    }

    @Test
    void getAllByOwnerId_WithoutPagination_StatePast() {
        long userId = 1L;
        String state = "PAST";
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());

        Mockito.when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(new User()));
        Mockito.when(bookingRepository.findPastByOwnerId(userId)).thenReturn(bookings);

        List<BookingDto> actualList = bookingService.getAllByOwnerId(userId, state, null, null);

        Mockito.verify(bookingRepository).findPastByOwnerId(userId);

    }

    @Test
    void getAllByOwnerId_WithoutPagination_StateFuture() {
        long userId = 1L;
        String state = "FUTURE";
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());

        Mockito.when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(new User()));
        Mockito.when(bookingRepository.findFutureByOwnerId(userId)).thenReturn(bookings);

        List<BookingDto> actualList = bookingService.getAllByOwnerId(userId, state, null, null);

        Mockito.verify(bookingRepository).findFutureByOwnerId(userId);
    }

    @Test
    void getAllByOwnerId_WithoutPagination_StateRejected() {
        long userId = 1L;
        String state = "REJECTED";
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());

        Mockito.when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(new User()));
        Mockito.when(bookingRepository.findRejectedByOwnerId(userId)).thenReturn(bookings);

        List<BookingDto> actualList = bookingService.getAllByOwnerId(userId, state, null, null);

        Mockito.verify(bookingRepository).findRejectedByOwnerId(userId);
    }

    @Test
    void getAllByOwnerId_WithoutPagination_StateWaiting() {
        long userId = 1L;
        String state = "WAITING";
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());

        Mockito.when(userRepository.findById(Long.valueOf(userId))).thenReturn(Optional.of(new User()));
        Mockito.when(bookingRepository.findWaitingByOwnerId(userId)).thenReturn(bookings);

        List<BookingDto> actualList = bookingService.getAllByOwnerId(userId, state, null, null);

        Mockito.verify(bookingRepository).findWaitingByOwnerId(userId);
    }

    @Test
    void getAllByOwnerId_WithPagination() {
        long userId = 1L;
        String state = "ALL";
        long from = 4L;
        long size = 2L;
        long start = from / size;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        Page<Booking> bookingPage = new PageImpl<>(bookings);

        Mockito.when(bookingRepository.findAllByOwnerIdWithPagination(userId, PageRequest.of(Math.toIntExact(start), Math.toIntExact(size), Sort.by("start_date").descending())))
                .thenReturn(bookingPage);

        List<BookingDto> actualList = bookingService.getAllByOwnerId(userId, state, from, size);

        Mockito.verify(bookingRepository).findAllByOwnerIdWithPagination(userId, PageRequest.of(Math.toIntExact(start), Math.toIntExact(size), Sort.by("start_date").descending()));

    }
}