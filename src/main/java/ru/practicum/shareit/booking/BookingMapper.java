package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.status.BookingStatus;
import ru.practicum.shareit.user.model.User;

import javax.annotation.processing.Generated;
import java.util.List;

@Mapper(componentModel = "spring")
@Generated("AutoGeneratedByMapStruct")
public interface BookingMapper {

    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    BookingDto toBookingDto(Booking booking);

    @Mapping(source = "booking.item.id", target = "itemId")
    @Mapping(source = "booking.booker.id", target = "bookerId")
    BookingNextLastDto toBookingLastNextDto(Booking booking);

    List<BookingDto> toBookingDto(List<Booking> listBookings);

    @Mapping(source = "bookingDto.id", target = "id")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "booker", target = "booker")
    @Mapping(source = "status", target = "status")
    Booking toBooking(BookingDto bookingDto, Item item, User booker, BookingStatus status);

    /* public static BookingDto toBookingResponseDto(Booking booking) {
        return new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            booking.getItem().getId(),
            booking.getItem(),
            booking.getBooker(),
            booking.getStatus()
        );
    } */
}
