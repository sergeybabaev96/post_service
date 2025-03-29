package faang.school.postservice.dto.kafka;

import faang.school.postservice.dto.post.PostResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public record PostPublishedEvent(

        PostResponseDto postDto,

        Long authorId,

        LocalDateTime createdAt,

        List<Long> followersIds,

        List<String> comments
) {
}
