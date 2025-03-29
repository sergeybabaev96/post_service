package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.PostPublishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static faang.school.postservice.utils.JsonUtils.getFieldFromJson;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostPublishedServiceImpl implements PostPublishedService {
    private static final String FIELD_ID = "id";
    private static final String FEED_KEY_PREFIX = "user:feed:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${feed.max-size}")
    private int feedMaxSize;

    @Override
    public void addPostsToFeed(PostPublishedEvent event) {
        event.followersIds().parallelStream()
                .forEach(followerId -> addPostWithLock(event, followerId));
    }

    private void addPostWithLock(PostPublishedEvent event, Long followerId) {
        String feedKey = FEED_KEY_PREFIX + followerId;
        String lockKey = "lock:" + feedKey;
        String lockValue = UUID.randomUUID().toString();
        try {
            boolean locked = Boolean.TRUE.equals(
                    redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS));
            if (!locked) {
                log.error("Could not acquire lock for feed: {}", feedKey);
                return;
            }
            redisTemplate.opsForZSet().add(feedKey,
                    mapObjectToString(event.postDto()), event.createdAt().toEpochSecond(ZoneOffset.UTC));
            checkPostDuplicate(event, feedKey);
            checkFeedSize(feedKey);
        } finally {
            String currentLockValue = redisTemplate.opsForValue().get(lockKey);
            if (lockValue.equals(currentLockValue)) {
                redisTemplate.delete(lockKey);
            }
        }
    }

    private void checkPostDuplicate(PostPublishedEvent event, String feedKey) {
        Set<String> cachedPosts = redisTemplate.opsForZSet().range(feedKey, 0, -1);
        if (cachedPosts == null) {
            return;
        }
        List<String> forRemoveFromCache = cachedPosts.stream()
                .filter(s -> {
                    String id = getFieldFromJson(s, FIELD_ID);
                    return id.equals(String.valueOf(event.postDto().id()));
                })
                .skip(1)
                .toList();
        if (!forRemoveFromCache.isEmpty()) {
            redisTemplate.opsForZSet().remove(feedKey, forRemoveFromCache.toArray());
        }
    }

    private void checkFeedSize(String feedKey) {
        long size = Optional.ofNullable(redisTemplate.opsForZSet().size(feedKey)).orElse(0L);
        if (size > feedMaxSize) {
            redisTemplate.opsForZSet().removeRange(feedKey, 0, size - feedMaxSize - 1);
        }
    }

    private String mapObjectToString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
