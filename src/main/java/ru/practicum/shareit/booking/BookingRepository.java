package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemIn(List<Item> items, Sort sort);

    List<Booking> findByBooker_IdOrderByStartDesc(long userId);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND " +
            "CURRENT_TIME BETWEEN b.start AND b.end")
    List<Booking> findCurrentBookerForUser(long userId, Sort sort);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND " +
            "CURRENT_TIME > b.end ORDER BY b.start DESC")
    List<Booking> getPastBooking(long userId);

    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(long userId, LocalDateTime startTime);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(long userId, Status status);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(long userId);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1 AND " +
            "CURRENT_TIME BETWEEN b.start AND b.end ORDER BY b.start DESC")
    List<Booking> findCurrentBookerForOwner(long userId);

    List<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(long userId, LocalDateTime time);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(long userId, Status status);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND " +
            "CURRENT_TIME > b.end ORDER BY b.start DESC")
    List<Booking> findPastBookerForOwner(long idUser);

    @Query(value = "SELECT b FROM Booking AS b WHERE ((b.item.id = ?1) AND " +
            "((b.start > ?2 AND b.start < ?3) OR (b.end > ?2 AND b.end < ?3))) ORDER BY b.start DESC")
    List<Booking> findBookingByItemToFree(long itemId, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.item.owner.id = ?2 " +
            "AND b.status = 'APPROVED' ORDER BY b.start DESC")
    List<Booking> findBookingByItemAndOwner(long itemId, long ownerId);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.booker.id = ?2 " +
            "AND b.status = 'APPROVED' AND b.end < CURRENT_TIME ORDER BY b.start DESC")
    List<Booking> findBookingByItem(long itemId, long bookerId);

    @Query(value = "SELECT b.*" +
            "         FROM bookings b" +
            "        INNER JOIN items i ON i.id = b.item_id AND i.id=:itemId " +
            "        WHERE b.status !='REJECTED' " +
            "          AND b.start_date < :dateTime " +
            "        ORDER BY b.start_date DESC " +
            "        LIMIT 1;",
            nativeQuery = true)
    Booking getLastItemBooking(long itemId, LocalDateTime dateTime);

    @Query(value = "SELECT b.*" +
            "         FROM bookings b" +
            "        INNER JOIN items i ON i.id = b.item_id AND i.id= :itemId " +
            "        WHERE b.status !='REJECTED' " +
            "          AND b.start_date > :dateTime " +
            "        ORDER BY b.start_date ASC " +
            "        LIMIT 1;",
            nativeQuery = true)
    Booking getNextItemBooking(long itemId, LocalDateTime dateTime);
}
