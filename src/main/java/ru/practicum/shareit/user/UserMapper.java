package ru.practicum.shareit.user;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "email", source = "entity.email")
    @Mapping(target = "name", source = "entity.name")
    User userDtoToUser(UserDto entity);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "email", source = "entity.email")
    @Mapping(target = "name", source = "entity.name")
    UserDto userToUserDto(User entity);
}
