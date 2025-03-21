package faang.school.postservice.dto.post;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {
    private long id;
    private long authorId;
    private long projectId;
    private String content;
    private boolean published;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
}
