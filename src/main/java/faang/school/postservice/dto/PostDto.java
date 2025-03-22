package faang.school.postservice.dto;

import java.time.LocalDateTime;

public record PostDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        boolean published,
        LocalDateTime publishedAt) {
}
