package faang.school.postservice.mapper;

import faang.school.postservice.dto.moderation.ItemToVerifyDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    ItemToVerifyDto toItemToVerifyDto(Comment comment);

    Stream<ItemToVerifyDto> toItemToVerifyDtosStream(Stream<Comment> comments);
}
