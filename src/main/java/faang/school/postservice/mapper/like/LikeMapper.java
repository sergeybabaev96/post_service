package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEventDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    LikeDto toLikeDto(Like like);

    @Mapping(target = "postId", source = "like.post.id")
    @Mapping(target = "postAuthorId", source = "like.post.authorId")
    @Mapping(target = "userId", source = "like.userId")
    @Mapping(target = "timestamp", source = "createdAt", qualifiedByName = "mapCreatedAtToTimestamp")
    LikeEventDto toLikeLikeEventDto(Like like);

    @Named("mapCreatedAtToTimestamp")
    default Timestamp mapCreatedAtToTimestamp(LocalDateTime createdAt) {
        return createdAt != null ? Timestamp.valueOf(createdAt) : new Timestamp(System.currentTimeMillis());
    }
}
