package faang.school.postservice.repository;

import java.util.List;

public interface RedisFeedRepository {
    void addPostsToFollowersFeed(Long postId, List<Long> followersIds);
}
