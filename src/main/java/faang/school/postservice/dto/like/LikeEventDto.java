package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LikeEventDto(
        @NotBlank
        long postAuthorId,
        @NotBlank
        long userId,
        @NotBlank
        LocalDateTime createdAt) {
}
