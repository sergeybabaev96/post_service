package faang.school.postservice.dto.feed;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserFeedDto {
    private Long postId;
    private Long userId;
    private Long projectId;
    private String authorName;
    private String content;
    private Integer likesCount;
    private List<UserFeedCommentDto> comments;
}
