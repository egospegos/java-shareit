package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
public interface ItemMapper {
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.name")
    @Mapping(target = "description", source = "entity.description")
    @Mapping(target = "available", source = "entity.available")
    @Mapping(target = "owner", source = "entity.owner")
    @Mapping(target = "request", source = "entity.request")
    Item itemDtoToItem(ItemDto entity);
}
