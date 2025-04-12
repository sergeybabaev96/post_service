package faang.school.postservice.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostNotification {
    @JsonProperty("postId")
    private Long postId;
    @JsonProperty("authorId")
    private Long authorId;
    @JsonProperty("projectId")
    private Long projectId;
    @JsonProperty("content")
    private String content;
}
