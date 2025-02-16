package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CommentEvent {
    private Long commentId;
    private String comment;
    private Long userId;
    private Long postId;
    private LocalDateTime createdAt;
}
