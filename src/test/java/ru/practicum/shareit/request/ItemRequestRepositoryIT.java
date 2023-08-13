package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ItemRequestRepositoryIT {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void save() {
    }

    @Test
    void findById() {
    }

    @Test
    void findAllByOwnerId() {
    }

    @Test
    void getAllWithPagination() {
    }
}