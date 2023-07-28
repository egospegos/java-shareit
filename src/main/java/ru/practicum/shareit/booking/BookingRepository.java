package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    @Query(value = " select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status from Bookings as b " +
            "where b.booker_id = ?1 order by b.start_date DESC", nativeQuery = true)
    List<Booking> findAllByBookerId(long bookerId);

    @Query(value = " select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status from Bookings as b " +
            "where b.booker_id = ?1 AND b.start_date < now() AND b.end_date > now() " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findCurrentByBookerId(long bookerId);

    @Query(value = " select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status from Bookings as b " +
            "where b.booker_id = ?1 AND b.start_date > now() " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findFutureByBookerId(long bookerId);

    @Query(value = " select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status from Bookings as b " +
            "where b.booker_id = ?1 AND b.end_date < now() " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findPastByBookerId(long bookerId);

    @Query(value = " select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status from Bookings as b " +
            "where b.booker_id = ?1 AND b.status = 'WAITING' " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findWaitingByBookerId(long bookerId);

    @Query(value = " select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status from Bookings as b " +
            "where b.booker_id = ?1 AND b.status = 'REJECTED' " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findRejectedByBookerId(long bookerId);

    @Query(value = " select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status from Bookings as b " +
            "INNER JOIN Items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 order by b.start_date DESC", nativeQuery = true)
    List<Booking> findAllByOwnerId(long ownerId);

    @Query(value = " select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status from Bookings as b " +
            "INNER JOIN Items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 AND b.start_date < now() AND b.end_date > now() " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findCurrentByOwnerId(long ownerId);

    @Query(value = " select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status from Bookings as b " +
            "INNER JOIN Items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 AND b.start_date > now() " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findFutureByOwnerId(long ownerId);

    @Query(value = " select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status from Bookings as b " +
            "INNER JOIN Items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 AND b.end_date < now() " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findPastByOwnerId(long ownerId);

    @Query(value = " select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status from Bookings as b " +
            "INNER JOIN Items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 AND b.status = 'WAITING' " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findWaitingByOwnerId(long ownerId);

    @Query(value = " select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status from Bookings as b " +
            "INNER JOIN Items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 AND b.status = 'REJECTED' " +
            "order by b.start_date DESC", nativeQuery = true)
    List<Booking> findRejectedByOwnerId(long ownerId);

    @Query(value = " select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status from Bookings as b " +
            "where b.item_id = ?1 order by b.start_date DESC", nativeQuery = true)
    List<Booking> findAllByItemId(long itemId);

    Booking findByItemId(long itemId);

    @Query(value = " select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status from Bookings as b " +
            "where b.item_id = ?1 AND b.booker_id = ?2 AND b.status != 'REJECTED' ", nativeQuery = true)
    List<Booking> findByItemIdAndUserId(long itemId, long userId);

    Booking save(Booking booking);

    Booking findById(long bookingId);
}
