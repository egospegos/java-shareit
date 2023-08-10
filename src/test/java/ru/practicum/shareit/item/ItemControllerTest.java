package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemService itemService;

    private final ItemDto itemDto = new ItemDto();

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @BeforeEach
    private void setItem() {
        itemDto.setName("item");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);


    }

    @Test
    void add() {
        long userId = 1L;

        when(itemService.addNewItem(userId, itemDto)).thenReturn(itemDto);

        ItemDto actualItemDto = itemController.add(userId, itemDto);

        assertEquals(itemDto, actualItemDto);
        Mockito.verify(itemService).addNewItem(userId, itemDto);

    }

    @Test
    void getAllByUserId() {

        long userId = 1L;
        List<ItemDtoWithBookings> itemList = new ArrayList<>();

        when(itemService.getAllByUserId(userId)).thenReturn(itemList);

        List<ItemDtoWithBookings> actualList = itemController.getAllByUserId(userId);

        assertEquals(itemList, actualList);
        Mockito.verify(itemService).getAllByUserId(userId);
    }

    @Test
    void getItem() {
        long userId = 0L;
        long itemId = 1L;
        Item item = itemMapper.itemDtoToItem(itemDto);
        ItemDtoWithBookings itemDtoWithBookings = itemMapper.itemToItemDtoWithBookings(item);

        when(itemService.get(itemId, userId)).thenReturn(itemDtoWithBookings);

        ItemDtoWithBookings actualItem = itemController.getItem(userId, itemId);

        assertEquals(itemDtoWithBookings, actualItem);

    }

    @Test
    void update() {
        long userId = 0L;
        long itemId = 1L;

        when(itemService.update(userId, itemId, itemDto)).thenReturn(itemDto);

        ItemDto actualItemDto = itemController.update(userId, itemId, itemDto);

        assertEquals(itemDto, actualItemDto);
        Mockito.verify(itemService).update(userId, itemId, itemDto);
    }

    @Test
    void search() {
        String text = "description";
        List<ItemDto> expectedList = new ArrayList<>();
        expectedList.add(itemDto);

        when(itemService.searchItems(text)).thenReturn(expectedList);

        List<ItemDto> actualList = itemController.search(text);

        assertEquals(expectedList, actualList);
        assertEquals(1, actualList.size());

    }

    @Test
    void addComment() {
        long userId = 0L;
        long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");

        when(itemService.addComment(userId, itemId, commentDto)).thenReturn(commentDto);

        CommentDto actualComment = itemController.addComment(userId, itemId, commentDto);

        assertEquals(commentDto, actualComment);
    }
}