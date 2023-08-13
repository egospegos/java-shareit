package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.util.common.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */

@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    //добавление нового запроса на бронирование
    @PostMapping
    @Validated({Marker.OnCreate.class})
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody @Valid BookingDto bookingDto) {
        return bookingService.addNewBooking(userId, bookingDto);
    }

    //подтверждение или отклонение запроса на бронирование
    @PatchMapping("/{bookingId}")
    public BookingDto setApproved(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable long bookingId, @RequestParam boolean approved) {
        return bookingService.setApproved(userId, bookingId, approved);
    }

    //получение данных о конкретном бронировании
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    //получение списка всех бронирований текущего пользователя
    @GetMapping
    public List<BookingDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "ALL") String state,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                           @RequestParam(defaultValue = "10") @Positive Long size) {
        return bookingService.getAllByUserId(userId, state, from, size);
    }

    //получение списка бронирований для всех вещей владельца (для владельца хотя бы одной вещи)
    @GetMapping("/owner")
    public List<BookingDto> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                            @RequestParam(defaultValue = "10") @Positive Long size) {
        return bookingService.getAllByOwnerId(ownerId, state, from, size);
    }

}
