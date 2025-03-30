package faang.school.postservice.service.feed;

import faang.school.postservice.repository.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;
    private final CacheService cacheService;

    public void addPostToFeed(List<Long> subscribersIds, Long postId, LocalDateTime publishedAt) {
        log.info("addPostToFeed subscribersIds {} Long postId {} publishedAt {} ", subscribersIds, postId, publishedAt);
        redisFeedRepository.addPost(subscribersIds, postId, publishedAt);
    }

    public void handlePostDeletion(Long postId) {
        log.info("handlePostDeletion postId {}", postId);
        cacheService.handlePostDeletion(postId);
    }
}
