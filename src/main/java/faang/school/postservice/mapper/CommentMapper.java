package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.moderation.ItemToVerifyDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "amountLikes",
            expression = "java(comment.getLikes() != null && !comment.getLikes().isEmpty() ? comment.getLikes().size() : 0)")
    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    ItemToVerifyDto toItemToVerifyDto(Comment comment);

    Stream<ItemToVerifyDto> toItemToVerifyDtosStream(Stream<Comment> comments);
}
