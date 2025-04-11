package faang.school.postservice.dto.analytic;

import faang.school.postservice.model.analytic.EventType;
import faang.school.postservice.model.analytic.Interval;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AnalyticFilterDto(
        @NotNull Long receiverId,
        @NotNull EventType eventType,
        Interval interval,
        LocalDateTime from,
        LocalDateTime to
) {
}
