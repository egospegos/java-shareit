package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ItemRequestMapper {
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "description", source = "entity.description")
    @Mapping(target = "created", source = "entity.created")
    ItemRequest itemRequestDtoToItemRequest(ItemRequestDto entity);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "description", source = "entity.description")
    @Mapping(target = "created", source = "entity.created")
    ItemRequestDto itemRequestToItemRequestDto(ItemRequest entity);
}
