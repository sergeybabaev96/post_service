package faang.school.postservice.dto.project.сomment;

import lombok.Builder;

@Builder
public record CommentDto(
        long postId,
        String content,
        long authorId

) {
}
