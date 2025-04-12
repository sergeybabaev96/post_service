package faang.school.postservice.dto.analytic;

import faang.school.postservice.model.analytic.EventType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AnalyticEventDto(
        @NotNull Long authorId,
        @NotNull Long receiverId,
        @NotNull EventType eventType,
        @NotNull LocalDateTime createdAt
) {
}
