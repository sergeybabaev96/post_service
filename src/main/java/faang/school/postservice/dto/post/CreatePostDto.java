package faang.school.postservice.dto.post;

import faang.school.postservice.validator.creatpost.AuthorOrProjectRequired;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@AuthorOrProjectRequired
public class CreatePostDto {
    Long id;
    @NotNull(message = "Content is required")
    String content;
    Long authorId;
    Long projectId;
}
