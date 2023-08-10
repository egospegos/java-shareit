package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Item save(Item item);

    Item findById(long itemId);

    @Query(value = " select i.id, i.name, i.description, i.is_available, i.owner_id, i.request_id from Items as i " +
            "where i.owner_id = ?1 ", nativeQuery = true)
    List<Item> findAllByUserId(long userId);

    @Query(value = " select i.id, i.name, i.description, i.is_available, i.owner_id, i.request_id from Items as i " +
            "where i.request_id = ?1 ", nativeQuery = true)
    List<Item> findAllByRequestId(long requestId);


    @Query(value = " select i.id, i.name, i.description, i.is_available, i.owner_id, i.request_id from Items as i " +
            "where ( upper(i.name) like upper(concat('%', ?1, '%')) " +
            " OR upper(i.description) like upper(concat('%', ?1, '%')) )" +
            " AND i.is_available = true", nativeQuery = true)
    List<Item> search(String text);
}
