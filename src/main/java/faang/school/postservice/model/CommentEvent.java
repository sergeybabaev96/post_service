package faang.school.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentEvent {
    private Long commentId;
    private Long postId;
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;
}
