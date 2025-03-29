package faang.school.postservice.dto.Post;

import faang.school.postservice.dto.comment.CommentForListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RedisHash("Posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCacheDto implements Serializable {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private Long likeCount;
    private Set<CommentForListDto> comments;
    private List<String> resourceKeys;

    @TimeToLive(unit = TimeUnit.HOURS)
    private Long hoursToExpire;
}
