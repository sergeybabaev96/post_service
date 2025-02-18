package faang.school.postservice.dto.comment;

public record CommentCreateEventDto(Long ownerPostUserId,
                                    Long postId,
                                    Long userIdCommentCreated) {
}
