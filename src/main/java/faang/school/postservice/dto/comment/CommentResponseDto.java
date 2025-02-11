package faang.school.postservice.dto.comment;


import faang.school.postservice.dto.like.comment.LikeCommentDtoResponse;
import faang.school.postservice.dto.post.PostDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CommentResponseDto(
        Long id,
        String content,
        Long authorId,
        List<LikeCommentDtoResponse> likeDtos,
        PostDto postDto,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
