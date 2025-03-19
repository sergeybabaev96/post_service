package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class PostDto {
    @Min(1) private Long id;
    @Min(0) private Long authorId;
    @Min(0) private Long projectId;
    @NotEmpty private String content;


    /*private boolean published;
    private boolean deleted;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;*/
}
