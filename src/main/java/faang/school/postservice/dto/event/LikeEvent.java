package faang.school.postservice.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.model.event.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeEvent {
    private long postId;
    private long authorId;
    private long userId;
    private LocalDateTime likedAt;
    private EventType type;
}
