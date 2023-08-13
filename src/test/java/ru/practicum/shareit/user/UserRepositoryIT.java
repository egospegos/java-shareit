package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;


    @Test
    void findAll() {
    }

    @Test
    void findById() {

    }

    @Test
    void save() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void findEmailById() {
    }

    @Test
    void findNameById() {
    }

    @Test
    void findAllEmails() {
    }

}