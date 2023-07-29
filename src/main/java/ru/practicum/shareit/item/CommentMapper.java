package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CommentMapper {
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "text", source = "entity.text")
    @Mapping(target = "authorName", expression = "java(entity.getAuthor().getName())")
    @Mapping(target = "created", source = "entity.created")
    CommentDto commentToCommentDto(Comment entity);


    @Mapping(target = "text", source = "entity.text")
    Comment commentDtoToComment(CommentDto entity);
}
