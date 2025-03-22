package faang.school.postservice.model;

import lombok.Data;

@Data
public class LikeEvent {
    private long authorId;
    private String postTitle;
    private String likerUsername;
}
