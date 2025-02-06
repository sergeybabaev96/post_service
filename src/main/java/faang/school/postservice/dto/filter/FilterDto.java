package faang.school.postservice.dto.filter;

public record FilterDto(
        Long authorId,
        Long projectId,
        boolean isPublished
) {
}
