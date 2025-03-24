package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CommentMapper {

        @Mapping(target = "likes", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        @Mapping(target = "largeImageFileKey", ignore = true)
        @Mapping(target = "smallImageFileKey", ignore = true)
        @Mapping(target = "post.id", source = "commentDto.postId")
        @Mapping(target = "createdAt", qualifiedByName = "mapCreatedAt")
        Comment toEntity(CommentDto commentDto, LocalDateTime createdAt);

        @Mapping(target = "postId", source = "post.id")
        @Mapping(target = "createdAt", source = "createdAt")
        CommentDto toDto(Comment comment);

        @Named("mapCreatedAt")
        default LocalDateTime mapCreatedAt(LocalDateTime createdAt) {
                return createdAt;
        }
}