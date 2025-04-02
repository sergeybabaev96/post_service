package faang.school.postservice.repository.cache;

import faang.school.postservice.config.redis.RedisKey;
import faang.school.postservice.model.event.PostEvent;
import faang.school.postservice.properties.CacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class PostRedisRepository implements PostCacheRepository {
    private final RedisTemplate<String, PostEvent> redisTemplate;
    private final CacheProperties cacheProperties;

    @Override
    public void cachePost(String key, PostEvent value) {
        redisTemplate.opsForSet().add(key, value);
        redisTemplate.expire(key, cacheProperties.getPostTtl());
    }

    @Override
    public Set<PostEvent> getMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public void removeFromSet(String key, PostEvent value) {
        redisTemplate.opsForSet().remove(key, value);
    }
}
