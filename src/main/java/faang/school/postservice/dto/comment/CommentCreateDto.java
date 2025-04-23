package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO для создания нового комментария.
 * Содержит данные, необходимые для создания комментария: текст, идентификатор автора и идентификатор поста.
 *
 * @author Zhltsk-V
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for creating a new comment")
public class CommentCreateDto {

    @Schema(
            description = "Comment text content",
            example = "This is an insightful comment about the post",
            minLength = 1,
            maxLength = 4096,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Comment text cannot be blank")
    @Size(min = 1, max = 4096,
            message = "Comment text must be between 1 and 4096 characters long")
    private String content;

    @Schema(
            description = "ID of the comment author",
            example = "42",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Author ID cannot be null")
    private Long authorId;

    @Schema(
            description = "ID of the post this comment belongs to",
            example = "123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Post ID cannot be null")
    private Long postId;

    @Schema(
            description = "Image file to attach (max 5MB)",
            format = "binary"
    )
    @Transient
    private MultipartFile imageFile;
}