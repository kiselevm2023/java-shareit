package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    default Optional<Booking>  findByIdOrThrow(long id) {
        return findById(id).map(Optional::of).orElseThrow(() -> new NotFoundException("Бронирование с id = " + id + " не найдено"));
    }

    default Booking searchByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Бронирование с id = " + id + " не найдено"));
    }

    List<Booking> findByBooker_Id(long userId, Sort sort);

    @Query(value = "SELECT b FROM Booking AS b " +
               "WHERE b.status = 'APPROVED' ")
    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sort);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND " +
            "CURRENT_TIME BETWEEN b.start AND b.end")
    List<Booking> findCurrentBookerForUser(long userId, Sort sort);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND " +
            "CURRENT_TIME > b.end")
    List<Booking> getPastBooking(long userId, Sort sort);

    List<Booking> findByBooker_IdAndStartAfter(long userId, LocalDateTime startTime, Sort sort);

    List<Booking> findByBooker_IdAndStatus(long userId, Status status, Sort sort);

    List<Booking> findByItem_Owner_Id(long userId, Sort sort);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1 AND " +
            "CURRENT_TIME BETWEEN b.start AND b.end")
    List<Booking> findCurrentBookerForOwner(long userId, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartAfter(long userId, LocalDateTime time, Sort sort);

    List<Booking> findByItem_Owner_IdAndStatus(long userId, Status status, Sort sort);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND " +
            "CURRENT_TIME > b.end")
    List<Booking> findPastBookerForOwner(long idUser, Sort sort);

    @Query(value = "SELECT b FROM Booking AS b WHERE ((b.item.id = ?1) AND " +
            "((b.start > ?2 AND b.start < ?3) OR (b.end > ?2 AND b.end < ?3)))")
    List<Booking> findBookingByItemToFree(long itemId, LocalDateTime start, LocalDateTime end, Sort sort);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.item.owner.id = ?2 " +
            "AND b.status = 'APPROVED'")
    List<Booking> findBookingByItemAndOwner(long itemId, long ownerId, Sort sort);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.booker.id = ?2 " +
            "AND b.status = 'APPROVED' AND b.end < CURRENT_TIME")
    List<Booking> findBookingByItem(long itemId, long bookerId, Sort sort);

    @Query(value = "SELECT b.*" +
            "         FROM bookings b" +
            "        INNER JOIN items i ON i.id = b.item_id AND i.id=:itemId " +
            "        WHERE b.status !='REJECTED' " +
            "          AND b.start_date <= :dateTime " +
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
