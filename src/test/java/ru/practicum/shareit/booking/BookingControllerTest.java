package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @InjectMocks
    private BookingController bookingController;
    @Mock
    private BookingService bookingService;

    @Test
    void add() {
        long userId = 1L;
        BookingDto expectedBooking = new BookingDto();

        Mockito.when(bookingService.addNewBooking(userId, expectedBooking)).thenReturn(expectedBooking);

        BookingDto actualBooking = bookingController.add(userId, expectedBooking);
        assertEquals(expectedBooking, actualBooking);


    }

    @Test
    void setApproved() {
        long userId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStatus(Status.APPROVED);

        Mockito.when(bookingService.setApproved(userId, bookingDto.getId(), true)).thenReturn(bookingDto);

        BookingDto actualBooking = bookingController.setApproved(userId, 1L, true);
        assertEquals(bookingDto, actualBooking);
    }

    @Test
    void getBooking() {
        BookingDto expectedBooking = new BookingDto();

        Mockito.when(bookingService.getBooking(1L, 1L)).thenReturn(expectedBooking);

        BookingDto actualBooking = bookingController.getBooking(1L, 1L);
        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void getAllByUserId() {
        List<BookingDto> expectedList = new ArrayList<>();

        Mockito.when(bookingService.getAllByUserId(1L, "ALL", null, null)).thenReturn(expectedList);

        List<BookingDto> actualList = bookingController.getAllByUserId(1L, "ALL", null, null);
        assertEquals(expectedList, actualList);
    }

    @Test
    void getAllByOwnerId() {
        List<BookingDto> expectedList = new ArrayList<>();

        Mockito.when(bookingService.getAllByOwnerId(1L, "ALL", null, null)).thenReturn(expectedList);

        List<BookingDto> actualList = bookingController.getAllByOwnerId(1L, "ALL", null, null);
        assertEquals(expectedList, actualList);
    }
}