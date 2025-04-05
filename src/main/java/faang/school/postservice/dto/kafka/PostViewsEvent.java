package faang.school.postservice.dto.kafka;

public record PostViewsEvent(

        Long userId,

        Long postId
) {
}
