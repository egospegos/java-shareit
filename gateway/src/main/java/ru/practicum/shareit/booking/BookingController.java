package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.util.common.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    //добавление нового запроса на бронирование
    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody @Valid BookingDto bookingDto) {
        return bookingClient.addBooking(userId, bookingDto);
    }

    //подтверждение или отклонение запроса на бронирование
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setApproved(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable long bookingId, @RequestParam boolean approved) {
        return bookingClient.setApproved(userId, bookingId, approved);
    }

    //получение данных о конкретном бронировании
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    //получение списка всех бронирований текущего пользователя
    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                 @RequestParam(defaultValue = "10") @Positive Long size) {
        return bookingClient.getAllBookingsByUserId(userId, state, from, size);
    }

    //получение списка бронирований для всех вещей владельца (для владельца хотя бы одной вещи)
    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                  @RequestParam(defaultValue = "10") @Positive Long size) {
        return bookingClient.getAllBookingsByOwnerId(ownerId, state, from, size);
    }
}
