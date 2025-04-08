package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.feed.PostCacheMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static faang.school.postservice.util.feed.RedisKeyUtils.feedCacheKey;

@RequiredArgsConstructor
@Slf4j
@Service
public class FeedCacheService {

    @Value("${spring.data.redis.feed-cache.size}")
    private int maxFeedSize;

    @Value("${spring.data.redis.feed-cache.key-prefix}")
    private String feedCacheKeyPrefix;

    @Value("${spring.data.redis.feed-cache.batch-size}")
    private int batchSize;

    @Value("${spring.data.redis.feed-cache.start-index}")
    private long startIndex;

    @Value("${spring.data.redis.feed-cache.rank-offset}")
    private long rankOffset;

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheService postCacheService;
    private final PostCacheMapper postCacheMapper;

    public void addPostIdToAuthorFollowers(Long postId, List<Long> followerIds, LocalDateTime publishedAt) {
        followerIds.forEach(followerId ->
                addPostIdToFollowerFeed(postId, followerId, publishedAt)
        );
    }

    public List<PostDto> getFeedByUserId(Long userId, Long postId) {
        List<Long> followerPostIds = getFollowerPostIds(userId, postId);
        return postCacheService.getPostCacheByIds(followerPostIds).stream()
                .map(postCacheMapper::toDto)
                .collect(Collectors.toList());
    }

    public void saveUserFeedHeat(FeedDto feedDto) {
        String key = feedCacheKey(feedCacheKeyPrefix, feedDto.getFollowerId());

        for (PostDto post : feedDto.getPosts()) {
            double score = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            redisTemplate.opsForZSet().add(key, post, score);
        }
    }

    private List<Long> getFollowerPostIds(Long userId, Long postId) {
        String key = feedCacheKey(feedCacheKeyPrefix, userId);

        if (postId == null) {
            return getFeedInRange(key, startIndex, startIndex + batchSize - 1);
        }

        Long rank = redisTemplate.opsForZSet().rank(key, postId);
        if (rank == null) {
            return getFeedInRange(key, startIndex, startIndex + batchSize - 1);
        }

        return getFeedInRange(key, rank + rankOffset, rank + rankOffset + batchSize - 1);
    }

    private List<Long> getFeedInRange(String key, long start, long end) {
        Set<Object> result = redisTemplate.opsForZSet().range(key, start, end);
        if (result == null) return Collections.emptyList();

        return result.stream()
                .map(obj -> (Long) obj)
                .collect(Collectors.toList());
    }

    private void addPostIdToFollowerFeed(Long postId, Long followerId, LocalDateTime publishedAt) {
        String key = feedCacheKey(feedCacheKeyPrefix, followerId);
        double score = publishedAt.toInstant(ZoneOffset.UTC).toEpochMilli();

        redisTemplate.opsForZSet().add(key, postId, score);

        Long size = redisTemplate.opsForZSet().zCard(key);
        if (size != null && size > maxFeedSize) {
            redisTemplate.opsForZSet().removeRange(key, startIndex, size - maxFeedSize);
        }
    }
}