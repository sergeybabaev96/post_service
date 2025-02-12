package faang.school.postservice.event;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NotNull
@RequiredArgsConstructor
@Builder
public class LikeEvent {
    @NotNull
    @PositiveOrZero
    private final Long postId;

    @NotNull
    @PositiveOrZero
    private final Long authorId;

    @NotNull
    @PositiveOrZero
    private final Long userId;

    @NotNull
    private final LocalDateTime likeTime;
}
