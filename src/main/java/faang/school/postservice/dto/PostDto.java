package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record PostDto(
        Long id,

        @NotNull(message = "Content cannot be null")
        @NotBlank(message = "Content cannot be blank")
        @Size(max = 5_000, message = "Content cannot be longer than 5,000 characterи")
        String content,

        @NotNull(message = "Author cannot be null")
        Long authorId,

        Long projectId,
        boolean published,
        LocalDateTime CreatedAt) {
}
