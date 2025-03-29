package faang.school.postservice.dto.post;

import java.time.LocalDateTime;

public record PostResponseDto(

        Long id,

        String content,

        Long authorId,

        Long projectId,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {

}
