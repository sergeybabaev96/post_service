package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.like.comment.LikeCommentDto;
import faang.school.postservice.dto.like.comment.LikeCommentDtoResponse;
import faang.school.postservice.dto.like.post.LikePostDto;
import faang.school.postservice.dto.like.post.LikePostDtoResponse;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class LikeMapper {

    @Autowired
    private PostMapper postMapper;
    @Lazy
    @Autowired
    private CommentMapper commentMapper;

    @Mapping(source = "post", target = "postResponseDto", qualifiedByName = "mapPost")
    public abstract LikePostDtoResponse toLikePostDtoResponse(Like like);

    @Mapping(source = "comment", target = "commentResponseDto", qualifiedByName = "mapComment")
    public abstract LikeCommentDtoResponse toLikeCommentDtoResponse(Like like);

    @Mapping(target = "post", ignore = true)
    public abstract Like toLike(LikePostDto likePostDto);

    @Mapping(target = "comment", ignore = true)
    public abstract Like toLike(LikeCommentDto likeCommentDto);

    @Named("mapPost")
    protected PostResponseDto mapPost(Post post) {
        if (post != null) {
            return postMapper.toPostResponseDto(post);
        }
        return null;
    }

    @Named("mapComment")
    protected CommentResponseDto mapPost(Comment comment) {
        if (comment != null) {
            return commentMapper.toCommentResponseDto(comment);
        }
        return null;
    }
}
