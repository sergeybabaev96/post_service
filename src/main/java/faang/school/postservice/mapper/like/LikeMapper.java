package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEventDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(target = "id", source = "like.id")
    @Mapping(target = "userId", source = "like.userId")
    LikeDto toLikeDto(Like like);

    @Mapping(target = "postAuthorId", source = "like.post.authorId")
    @Mapping(target = "userId", source = "like.userId")
    @Mapping(target = "createdAt", source = "createdAt")
    LikeEventDto toLikeEventDto(Like like);
}
