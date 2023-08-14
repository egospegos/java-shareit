package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    ItemRequest save(ItemRequest itemRequest);

    ItemRequest findById(long itemRequestId);

    @Query(value = " select r.id, r.description, r.created, r.requester_id from Requests as r " +
            "where r.requester_id = ?1 ", nativeQuery = true)
    List<ItemRequest> findAllByOwnerId(long userId);


    @Query(value = " select r.id, r.description, r.created, r.requester_id from Requests as r " +
            "where r.requester_id != ?1 ", nativeQuery = true)
    Page<ItemRequest> getAllWithPagination(Long ownerId, Pageable pageable);
}
