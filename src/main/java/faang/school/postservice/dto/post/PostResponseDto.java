package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@RequiredArgsConstructor
public class PostResponseDto {

    @JsonProperty("id")
    private final Long id;

    @JsonProperty("content")
    private final String content;

    @JsonProperty("authorId")
    private final Long authorId;

    @JsonProperty("projectId")
    private final Long projectId;

    @JsonProperty("createdAt")
    private final LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private final LocalDateTime updatedAt;
}
