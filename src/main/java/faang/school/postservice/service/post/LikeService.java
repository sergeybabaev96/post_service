package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.LikeDto;

public interface LikeService {

    LikeDto addLikeToPost(long postId);
}
