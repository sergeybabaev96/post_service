package faang.school.postservice.mapper;

import faang.school.postservice.dto.feed.UserFeedCommentDto;
import faang.school.postservice.dto.feed.UserFeedDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.CacheAuthor;
import faang.school.postservice.model.cache.CacheComment;
import faang.school.postservice.model.cache.CachePost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface NewsFeedMapper {

    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "timeToLive", source = "ttl")
    CachePost toCache(Post post, Long ttl);

    @Mapping(target = "userId", source = "userDto.id")
    @Mapping(target = "authorName", source = "userDto.username")
    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "timeToLeave", source = "ttl")
    CacheAuthor toCache(UserDto userDto, Long ttl);

    @Mapping(target = "projectId", source = "projectDto.id")
    @Mapping(target = "authorName", source = "projectDto.title")
    @Mapping(target = "timeToLeave", source = "ttl")
    CacheAuthor toCache(ProjectDto projectDto, Long ttl);

    @Mapping(target = "likes", source = "likesCount")
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "id", source = "id")
    CacheComment toCache(Comment comment);

    @Mapping(target = "likesCount", source = "postLikesCount")
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorName", source = "author.authorName")
    @Mapping(target = "content", source = "post.content")
    @Mapping(target = "userId", source = "author.userId")
    @Mapping(target = "projectId", source = "author.projectId")
    @Mapping(target = "comments", source = "comments", qualifiedByName = "toDtoComment")
    UserFeedDto toDto(CachePost post, Integer postLikesCount, CacheAuthor author, List<CacheComment> comments);

    @Mapping(target = "likesCount", source = "likes")
    @Named("toDtoComment")
    UserFeedCommentDto toDto(CacheComment comment);
}
