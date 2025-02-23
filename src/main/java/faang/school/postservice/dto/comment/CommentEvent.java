package faang.school.postservice.dto.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentEvent(
        Long postId,
        Long authorId,
        Long commentId,
        LocalDateTime timestamp
) {
}
