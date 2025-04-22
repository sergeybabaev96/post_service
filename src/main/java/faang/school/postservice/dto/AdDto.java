package faang.school.postservice.dto;

import faang.school.postservice.model.Post;

import java.time.LocalDateTime;

public record AdDto(
        Long id,
        Long postId,
        long buyerId,
        long appearancesLeft,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
