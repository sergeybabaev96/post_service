package faang.school.postservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostDto(Long id, String content, Long authorId, Long projectId,
                      List<Long> likesId, List<Long> commentsId, List<Long> albumsId,
                      Long adId, List<Long> resourcesId, boolean published,
                      LocalDateTime publishedAt, LocalDateTime scheduledAt,
                      boolean deleted) {
}
