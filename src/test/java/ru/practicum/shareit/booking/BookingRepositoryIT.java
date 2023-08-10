package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BookingRepositoryIT {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findAllByBookerId() {
    }

    @Test
    void findCurrentByBookerId() {
    }

    @Test
    void findFutureByBookerId() {
    }

    @Test
    void findPastByBookerId() {
    }

    @Test
    void findWaitingByBookerId() {
    }

    @Test
    void findRejectedByBookerId() {
    }

    @Test
    void findAllByOwnerId() {
    }

    @Test
    void findCurrentByOwnerId() {
    }

    @Test
    void findFutureByOwnerId() {
    }

    @Test
    void findPastByOwnerId() {
    }

    @Test
    void findWaitingByOwnerId() {
    }

    @Test
    void findRejectedByOwnerId() {
    }

    @Test
    void findAllByItemId() {
    }

    @Test
    void findByItemId() {
    }

    @Test
    void findByItemIdAndUserId() {
    }

    @Test
    void findAllByBookerIdWithPagination() {
    }

    @Test
    void findAllByOwnerIdWithPagination() {
    }

    @Test
    void save() {
    }

    @Test
    void findById() {
    }
}