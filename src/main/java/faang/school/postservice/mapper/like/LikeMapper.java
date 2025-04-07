package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(source = "authorId", target = "userId")
    @Mapping(source = "commentId", target = "comment.id")
    @Mapping(source = "postId", target = "post.id")
    Like toEntity(LikeDto likeDto);

    @Mapping(source = "userId", target = "authorId")
    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    LikeDto toDto(Like like);
}
