package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ItemRepositoryIT {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void save() {
    }

    @Test
    void findById() {
    }

    @Test
    void findAllByUserId() {
    }

    @Test
    void findAllByRequestId() {
    }

    @Test
    void search() {
    }
}