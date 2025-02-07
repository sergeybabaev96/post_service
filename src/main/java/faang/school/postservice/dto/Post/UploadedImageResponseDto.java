package faang.school.postservice.dto.Post;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UploadedImageResponseDto {
    private Long id;
    private Long postId;
    private String key;
    private String name;
    private String type;
    private LocalDateTime createdAt;


}
