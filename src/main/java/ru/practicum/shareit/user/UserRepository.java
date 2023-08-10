package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();

    User findById(long userId);

    User save(User user);

    void deleteById(long userId);

    @Query(value = "select u.email from Users as u where u.id = ?1", nativeQuery = true)
    String findEmailById(long id);

    @Query(value = "select u.name from Users as u where u.id = ?1", nativeQuery = true)
    String findNameById(long id);

    @Query(value = "select u.email from Users as u", nativeQuery = true)
    List<String> findAllEmails();
}
