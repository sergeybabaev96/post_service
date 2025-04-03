package faang.school.postservice.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserFeedCommentDto {
    private Long id;
    private String content;
    private int likesCount;
    private Long authorId;
}
