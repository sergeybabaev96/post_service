package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostEvent {
    private Long postId;
    private Long authorId;
    private List<Long> subscribersIds;
}
