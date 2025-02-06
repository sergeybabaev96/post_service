package faang.school.postservice.dto.post;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostUpdateDto {
    @Size(max = 4096)
    private String content;

    @FutureOrPresent(message = "Дата запланированной публикации не может быть в прошлом")
    private LocalDateTime scheduledAt;
}
