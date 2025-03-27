package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikePostEvent {
    private long postId;
    private long postAuthorId;
    private long likeUserId;
}
