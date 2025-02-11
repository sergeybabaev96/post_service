package faang.school.postservice.dto.like.comment;

import faang.school.postservice.dto.comment.CommentResponseDto;

public record LikeCommentDtoResponse(

        Long id, Long userId, Long postId, CommentResponseDto commentResponseDto) {
}
