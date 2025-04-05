package faang.school.postservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisFeedRepositoryImpl implements RedisFeedRepository {
    @Value("${spring.data.redis.properties.feed-collection.name}")
    private String feedCollectionName;
    @Value("${spring.data.redis.properties.feed-collection.max-cache-size}")
    private long maxCacheSize;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void addPostsToFollowersFeed(Long postId, List<Long> followersIds) {
        for (long id : followersIds) {
            String postsKey = feedCollectionName + id;
            redisTemplate.execute(new SessionCallback<>() {
                @Override
                public Object execute(RedisOperations operations) {
                    List<Object> executionResult;
                    do {
                        redisTemplate.watch(postsKey);
                        redisTemplate.multi();

                        long postsInFeed = Optional.ofNullable(redisTemplate.opsForZSet().size(postsKey)).orElse(0L);
                        if (postsInFeed >= maxCacheSize) {
                            redisTemplate.opsForZSet().removeRange(postsKey, 0, postsInFeed - maxCacheSize);
                        }
                        redisTemplate.opsForZSet().add(postsKey, postId, System.currentTimeMillis());

                        executionResult = redisTemplate.exec();
                        redisTemplate.unwatch();
                    } while (executionResult.get(0) == null);
                    return executionResult;
                }
            });
        }
    }
}
