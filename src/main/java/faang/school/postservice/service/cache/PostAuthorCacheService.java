package faang.school.postservice.service.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PostAuthorCacheService {

    public final RedisTemplate<String, Long> redisTemplate;
    private final String authorsKey;
    private final Duration ttl;

    public PostAuthorCacheService(RedisTemplate<String, Long> redisTemplate,
                                  @Value("${cache.authors.collection:comment_authors}") String authorsKey,
                                  @Value("${cache.authors.ttl:86400}") long ttlSeconds) {
        this.redisTemplate = redisTemplate;
        this.authorsKey = authorsKey;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    @Async
    public void cachePostAuthor(Long authorId) {
        redisTemplate.opsForSet().add(authorsKey, authorId);
        redisTemplate.expire(authorsKey, ttl);
    }
}
