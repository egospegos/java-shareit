package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void get_WithoutBookingAndWithoutComments() {
        long itemId = 1L;
        long userId = 1L;
        User user = new User();
        user.setId(2L);
        Item item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setOwner(user);
        List<Comment> comments = new ArrayList<>();

        Mockito.when(itemRepository.findById(itemId)).thenReturn(item);
        Mockito.when(itemRepository.findById(Long.valueOf(itemId))).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);

        ItemDtoWithBookings actualItem = itemService.get(itemId, userId);
        ItemDtoWithBookings expectedItem = itemMapper.itemToItemDtoWithBookings(item);

        assertEquals(expectedItem, actualItem);

    }

    @Test
    void get_WithoutBookingAndWithComments() {
        long itemId = 1L;
        long userId = 1L;
        User user = new User();
        user.setId(2L);
        Item item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setOwner(user);
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setText("text comment");
        comment.setAuthor(user);
        comments.add(comment);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(item);
        Mockito.when(itemRepository.findById(Long.valueOf(itemId))).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);

        ItemDtoWithBookings actualItem = itemService.get(itemId, userId);
        ItemDtoWithBookings expectedItem = itemMapper.itemToItemDtoWithBookings(item);
        expectedItem.getComments().add(commentMapper.commentToCommentDto(comment));

        assertEquals(expectedItem, actualItem);

    }

    @Test
    void get_WithBookingAndWithoutComments() {
        long itemId = 1L;
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setOwner(user);
        List<Comment> comments = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookings.add(booking);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(item);
        Mockito.when(itemRepository.findById(Long.valueOf(itemId))).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);
        Mockito.when(bookingRepository.findAllByItemId(itemId)).thenReturn(bookings);

        ItemDtoWithBookings actualItem = itemService.get(itemId, userId);
        ItemDtoWithBookings expectedItem = itemMapper.itemToItemDtoWithBookings(item);
        expectedItem.setLastBooking(bookingMapper.bookingToBookingShort(bookings.get(0)));

        assertEquals(expectedItem, actualItem);

    }

    @Test
    void get_WithNotValidItemId() {
        long itemId = -1L;
        long userId = 1L;
        Item item = new Item();
        item.setName("name");
        item.setDescription("description");

        Mockito.when(itemRepository.findById(Long.valueOf(itemId))).thenReturn(Optional.of(item));

        assertThrows(DataNotFoundException.class,
                () -> itemService.get(itemId, userId));


    }

    @Test
    void getAllByUserId_WithBookings() {
        long itemId = 1L;
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        item.setName("name");
        item.setDescription("description");
        item.setOwner(user);
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<Booking> bookings = new ArrayList<>();
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookings.add(booking);


        Mockito.when(itemRepository.findAllByUserId(userId)).thenReturn(items);
        Mockito.when(bookingRepository.findAllByItemId(itemId)).thenReturn(bookings);

        List<ItemDtoWithBookings> actualItems = itemService.getAllByUserId(userId);
        List<ItemDtoWithBookings> expectedItems = new ArrayList<>();
        expectedItems.add(itemMapper.itemToItemDtoWithBookings(item));
        expectedItems.get(0).setLastBooking(bookingMapper.bookingToBookingShort(bookings.get(0)));

        assertEquals(expectedItems, actualItems);

    }

    @Test
    void getAllByUserId_WithBookingsMoreThen1() {
        long itemId = 1L;
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        item.setName("name");
        item.setDescription("description");
        item.setOwner(user);
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<Booking> bookings = new ArrayList<>();
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookings.add(booking);
        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(LocalDateTime.now().minusDays(3));
        booking2.setEnd(LocalDateTime.now().minusDays(2));
        bookings.add(booking2);


        Mockito.when(itemRepository.findAllByUserId(userId)).thenReturn(items);
        Mockito.when(bookingRepository.findAllByItemId(itemId)).thenReturn(bookings);

        List<ItemDtoWithBookings> actualItems = itemService.getAllByUserId(userId);
        List<ItemDtoWithBookings> expectedItems = new ArrayList<>();
        expectedItems.add(itemMapper.itemToItemDtoWithBookings(item));
        expectedItems.get(0).setLastBooking(bookingMapper.bookingToBookingShort(bookings.get(0)));

        Mockito.verify(bookingRepository).findAllByItemId(itemId);

    }

    @Test
    void addNewItem_WithRequestId() {
        long userId = 1L;
        long itemId = 1L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        ItemDto itemDto = itemMapper.itemToItemDto(item);
        itemDto.setRequestId(1L);
        ItemRequest request = new ItemRequest();
        request.setId(1L);


        Mockito.when(itemRequestRepository.findById(1L)).thenReturn(request);
        Mockito.when(userRepository.findById(userId)).thenReturn(user);

        item.setItemRequest(request);
        item.setOwner(user);
        Mockito.when(itemRepository.save(item)).thenReturn(item);

        ItemDto actualItem = itemService.addNewItem(userId, itemDto);
        ItemDto expectedItem = itemMapper.itemToItemDtoWithRequestId(item);

        assertEquals(expectedItem, actualItem);

    }

    @Test
    void addNewItem_WithoutRequestId() {
        long userId = 1L;
        long itemId = 1L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        ItemDto itemDto = itemMapper.itemToItemDto(item);
        itemDto.setRequestId(0);


        Mockito.when(userRepository.findById(userId)).thenReturn(user);

        item.setOwner(user);
        Mockito.when(itemRepository.save(item)).thenReturn(item);

        ItemDto actualItem = itemService.addNewItem(userId, itemDto);
        ItemDto expectedItem = itemMapper.itemToItemDto(item);

        assertEquals(expectedItem, actualItem);

    }

    @Test
    void update_withNewName() {
        long userId = 1L;
        long itemId = 1L;
        User user = new User();
        user.setId(userId);
        Item oldItem = new Item();
        oldItem.setId(itemId);
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setOwner(user);
        ItemDto oldItemDto = itemMapper.itemToItemDto(oldItem);
        Item newItem = new Item();
        newItem.setOwner(user);
        newItem.setName("new name");


        Mockito.when(itemRepository.findById(Long.valueOf(itemId))).thenReturn(Optional.of(oldItem));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(newItem);
        Mockito.when(userRepository.findById(userId)).thenReturn(user);
        oldItem.setName("new name");
        Mockito.when(itemRepository.save(oldItem)).thenReturn(oldItem);

        ItemDto actualItem = itemService.update(userId, itemId, oldItemDto);
        oldItemDto.setName("new name");
        assertEquals(oldItemDto, actualItem);
    }

    @Test
    void update_withNewDescriptionAndNewAvailable() {
        long userId = 1L;
        long itemId = 1L;
        User user = new User();
        user.setId(userId);
        Item oldItem = new Item();
        oldItem.setId(itemId);
        oldItem.setName("name");
        oldItem.setOwner(user);
        ItemDto oldItemDto = itemMapper.itemToItemDto(oldItem);
        Item newItem = new Item();
        newItem.setOwner(user);
        newItem.setDescription("new description");
        newItem.setAvailable(false);

        Mockito.when(itemRepository.findById(Long.valueOf(itemId))).thenReturn(Optional.of(oldItem));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(newItem);
        Mockito.when(userRepository.findById(userId)).thenReturn(user);
        oldItem.setDescription("new description");
        oldItem.setAvailable(false);
        Mockito.when(itemRepository.save(oldItem)).thenReturn(oldItem);

        ItemDto actualItem = itemService.update(userId, itemId, oldItemDto);
        oldItemDto.setDescription("new description");
        oldItemDto.setAvailable(false);
        assertEquals(oldItemDto, actualItem);
    }

    @Test
    void update_withNotValidUserId() {
        long userId = 1L;
        long itemId = 1L;
        User user = new User();
        user.setId(userId);
        Item oldItem = new Item();
        oldItem.setId(itemId);
        oldItem.setName("name");
        oldItem.setOwner(user);


        Mockito.when(itemRepository.findById(Long.valueOf(itemId))).thenReturn(Optional.of(oldItem));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(oldItem);

        assertThrows(DataNotFoundException.class,
                () -> itemService.update(2L, itemId, itemMapper.itemToItemDto(oldItem)));
    }

    @Test
    void searchItems_whenTextIsEmpty() {
        List<ItemDto> expectedList = new ArrayList<>();
        List<ItemDto> actualList = itemService.searchItems("");

        assertEquals(expectedList, actualList);
    }

    @Test
    void searchItems() {
        String searchText = "description";
        List<Item> expectedList = new ArrayList<>();
        Item item = new Item();
        expectedList.add(item);
        List<ItemDto> expectedListDto = new ArrayList<>();
        expectedListDto.add(itemMapper.itemToItemDto(item));

        Mockito.when(itemRepository.search(searchText)).thenReturn(expectedList);
        List<ItemDto> actualList = itemService.searchItems(searchText);

        assertEquals(expectedListDto, actualList);
    }

    @Test
    void addComment() {
        long itemId = 1L;
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("name user");
        Item item = new Item();
        item.setId(itemId);
        item.setName("name");
        item.setDescription("description");
        item.setOwner(user);
        List<Booking> bookings = new ArrayList<>();
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookings.add(booking);

        Comment comment = new Comment();
        comment.setId(2L);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now().withNano(0).plusSeconds(1));

        Mockito.when(bookingRepository.findByItemIdAndUserId(itemId, userId)).thenReturn(bookings);
        Mockito.when(itemRepository.findById(itemId)).thenReturn(item);
        Mockito.when(userRepository.findById(userId)).thenReturn(user);
        Mockito.when(commentRepository.save(comment)).thenReturn(comment);

        CommentDto expectedComment = commentMapper.commentToCommentDto(comment);
        CommentDto actualComment = itemService.addComment(userId, itemId, expectedComment);

        assertEquals(expectedComment, actualComment);


    }
}