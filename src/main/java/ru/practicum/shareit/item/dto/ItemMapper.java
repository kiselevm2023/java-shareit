package ru.practicum.shareit.item.dto;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.BookingNextLastDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(source = "item.request.id", target = "requestId")
    @Named("toItemDto")
    ItemDto toItemDto(Item item);

    @Mapping(source = "item.id", target = "id")
    ItemWithBookingsDateDto toItemWithBookingDto(Item item,
                                                 BookingNextLastDto lastBooking,
                                                 BookingNextLastDto nextBooking, List<CommentDto> comments);


    @IterableMapping(qualifiedByName = "toItemDto")
    List<ItemDto> toItemDto(List<Item> listItems);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "itemDto.name", target = "name")
    Item toItem(User owner, ItemDto itemDto);

    public static ItemWithBookingsDateDto toItemResponseDtoWithBookings(Item item, List<CommentDto> comments) {
        return new ItemWithBookingsDateDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                comments
        );
    }

    public static ItemWithBookingsDateDto mapToItemDto(Item item, BookingNextLastDto lastBooking, BookingNextLastDto nextBooking, List<CommentDto> commentsDto) {
        return new ItemWithBookingsDateDto(
        item.getId(),
        item.getName(),
        item.getDescription(),
        item.getAvailable(),
        lastBooking,
        nextBooking,
        commentsDto
        );
    }


    public static Item first(User owner, ItemDto itemDto) {
        if ( owner == null && itemDto == null ) {
            return null;
        }

        Item.ItemBuilder item = Item.builder();

        if ( itemDto != null ) {
            item.name( itemDto.getName() );
            item.description( itemDto.getDescription() );
            item.available( itemDto.getAvailable() );
            item.request( itemRequestDtoToItemRequestCreate( itemDto.getRequest() ) );


        }
        item.owner( owner );

        return item.build();
    }

    static ItemRequest  itemRequestDtoToItemRequestCreate(ItemRequestDto itemRequestDto) {
        if ( itemRequestDto == null ) {
            return null;
        }

        ItemRequest.ItemRequestBuilder itemRequest = ItemRequest.builder();

        itemRequest.id( itemRequestDto.getId() );
        itemRequest.description( itemRequestDto.getDescription() );
        itemRequest.requestor( userDtoToUserCreate( itemRequestDto.getRequestor() ) );
        itemRequest.created( itemRequestDto.getCreated() );

        return itemRequest.build();
    }

    static User userDtoToUserCreate(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( userDto.getId() );
        user.name( userDto.getName() );
        user.email( userDto.getEmail() );

        return user.build();
    }


    public static ItemDto second(Item item) {
        if ( item == null ) {
            return null;
        }

        ItemDto.ItemDtoBuilder itemDto = ItemDto.builder();

        itemDto.requestId( item.getRequest().getId() );
        itemDto.id( item.getId() );
        itemDto.name( item.getName() );
        itemDto.description( item.getDescription() );
        itemDto.available( item.getAvailable() );
        itemDto.request( itemRequestToItemRequestDtoCreate( item.getRequest() ) );

        return itemDto.build();
    }

    public static ItemRequestDto itemRequestToItemRequestDtoCreate(ItemRequest itemRequest) {
        if ( itemRequest == null ) {
            return null;
        }

        ItemRequestDto.ItemRequestDtoBuilder itemRequestDto = ItemRequestDto.builder();

        itemRequestDto.id( itemRequest.getId() );
        itemRequestDto.description( itemRequest.getDescription() );
        itemRequestDto.requestor( userToUserDtoCreate( itemRequest.getRequestor() ) );
        itemRequestDto.created( itemRequest.getCreated() );

        return itemRequestDto.build();
    }

    public static UserDto userToUserDtoCreate(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        if ( user.getId() != null ) {
            userDto.id( user.getId() );
        }
        userDto.name( user.getName() );
        userDto.email( user.getEmail() );

        return userDto.build();
    }
}