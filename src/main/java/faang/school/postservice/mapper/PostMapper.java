package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    Post createDtoToEntity(PostCreateDto postCreateDto);

    @Mapping(source = "likes", target = "totalLikes")
    @Mapping(source = "comments", target = "commentIds")
    @Mapping(source = "resources", target = "resourcesIds")
    @Mapping(source = "ad.id", target = "adId")
    PostViewDto toViewDto(Post post);

    void update(PostUpdateDto source, @MappingTarget Post target);

    default long getTotalLikes(List<Like> likes) {
        return likes.size();
    }

    default List<Long> getCommentIds(List<Comment> comments) {
        return comments.stream().map(Comment::getId).toList();
    }

    default List<Long> getResourcesIds(List<Resource> resources) {
        return resources.stream().map(Resource::getId).toList();
    }
}
