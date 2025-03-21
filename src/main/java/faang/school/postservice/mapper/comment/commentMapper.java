package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.project.сomment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface commentMapper {

    @Mapping(source = "post.id", target = "postId") 
    @Mapping(source = "authorId", target = "authorId")
    @Mapping(source = "content", target = "content")
    CommentDto toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post.id", source = "postId")
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "content", source = "content")
    Comment toEntity(CommentDto commentDto);
}