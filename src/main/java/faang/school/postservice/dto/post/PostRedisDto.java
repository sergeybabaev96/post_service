package faang.school.postservice.dto.post;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostRedisDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        boolean published,
        LocalDateTime publishedAt,
        LocalDateTime scheduledAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
