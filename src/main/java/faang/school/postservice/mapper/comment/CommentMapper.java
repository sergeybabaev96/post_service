package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.сomment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

        @Mapping(target = "likes", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        @Mapping(target = "largeImageFileKey", ignore = true)
        @Mapping(target = "smallImageFileKey", ignore = true)
        @Mapping(target = "post.id", source = "postId")
        @Mapping(target = "createdAt", ignore = true)
        Comment toEntity(CommentDto commentDto);

        @Mapping(target = "postId", source = "post.id")
        @Mapping(target = "createdAt", source = "createdAt")
        CommentDto toDto(Comment comment);
}