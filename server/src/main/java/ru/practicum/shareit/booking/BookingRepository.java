package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.status.BookingStatus;

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

    default Booking findByIdAndOwnerIdNotOrThrow(Long bookingId, Long ownerId) {
        return findBookingByBookingIdAndOwnerId(bookingId, ownerId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sort);

    @Query("select b from Booking b join fetch b.item i where b.id = :bookingId and i.owner.id = :ownerId")
    Optional<Booking> findBookingByBookingIdAndOwnerId(@Param("bookingId") Long bookingId,
                                                       @Param("ownerId") Long ownerId);

    @Query("select b from Booking b " +
            "join fetch b.item i " +
            "where b.id = :bookingId and (i.owner.id = :ownerId or b.booker.id  = :ownerId)")
    Optional<Booking> findBookingByBookingIdAndOwnerIdOrOwnerItemId(@Param("bookingId") Long bookingId,
                                                                    @Param("ownerId") Long ownerId);

    @Query("select b from Booking b " +
            "join b.item i " +
            "where b.booker.id = :userId " +
            "and (:state = 'ALL' or " +
            "(:state = 'CURRENT' and b.start <= current_timestamp and b.end >= current_timestamp) or " +
            "(:state = 'PAST' and b.end < current_timestamp) or " +
            "(:state = 'FUTURE' and b.start > current_timestamp) or " +
            "(:state = 'WAITING' and b.status = 'WAITING') or " +
            "(:state = 'REJECTED' and b.status = 'REJECTED')) " +
            "order by b.start desc")
    Page<Booking> findUserBookingsWithState(
            @Param("userId") Long userId,
            @Param("state") String state,
            Pageable pageable);

    @Query("select b from Booking b " +
            "join b.item i " +
            "where i.owner.id = :ownerId " +
            "and (:state = 'ALL' or " +
            "(:state = 'CURRENT' and b.start <= current_timestamp and b.end >= current_timestamp) or " +
            "(:state = 'PAST' and b.end < current_timestamp) or " +
            "(:state = 'FUTURE' and b.start > current_timestamp) or " +
            "(:state = 'WAITING' and b.status = 'WAITING') or " +
            "(:state = 'REJECTED' and b.status = 'REJECTED')) " +
            "order by b.start desc")
    Page<Booking> findOwnerBookingsWithState(
            @Param("ownerId") Long ownerId,
            @Param("state") String state,
            Pageable pageable);

    @Query("select b from Booking b " +
            "join fetch b.item i " +
            "where i.id = :itemId " +
            "order by b.start desc")
    List<Booking> findBookingByItemId(@Param("itemId") Long itemId);

    boolean existsByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end);

    Optional<Booking> findTopByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime localDateTime);

    Optional<Booking> findTopByItemIdAndStatusNotAndStartAfterOrderByStartAsc(
            Long itemId,
            BookingStatus status,
            LocalDateTime localDateTime);

    @Query(value = "SELECT b.*" +
            "         FROM bookings b" +
            "        INNER JOIN items i ON i.id = b.item_id AND i.id= :itemId " +
            "        WHERE b.status !='REJECTED' " +
            "          AND b.start_date > :dateTime " +
            "        ORDER BY b.start_date ASC " +
            "        LIMIT 1;",
            nativeQuery = true)
    Booking getNextItemBooking(long itemId, LocalDateTime dateTime);

    @Query(value = "SELECT b.*" +
            "         FROM bookings b" +
            "        INNER JOIN items i ON i.id = b.item_id AND i.id=:itemId " +
            "        WHERE b.status !='REJECTED' " +
            "          AND b.start_date <= :dateTime " +
            "        ORDER BY b.start_date DESC " +
            "        LIMIT 1;",
            nativeQuery = true)
    Booking getLastItemBooking(long itemId, LocalDateTime dateTime);
}