package faang.school.postservice.dto.like.post;

import faang.school.postservice.dto.post.PostResponseDto;


public record LikePostDtoResponse(

        Long id, Long userId, PostResponseDto postResponseDto) {
}
