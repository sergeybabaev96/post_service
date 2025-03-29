package faang.school.postservice.dto.kafka;

public record LikeEvent(

        Long authorId,

        Long postId
) {
}
