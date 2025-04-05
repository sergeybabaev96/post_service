package faang.school.postservice.service.event;

import faang.school.postservice.dto.kafka.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeEventServiceImpl implements LikeEventService {

    private static final String LIKE_KEY_PREFIX = "post:likes:";
    private static final String LIKE_DATA_KEY_PREFIX = "like:data:";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void addLikeToPost(LikeEvent likeEvent) {
        String likeKey = LIKE_KEY_PREFIX + likeEvent.postId();
        String likeDataKey = LIKE_DATA_KEY_PREFIX + likeEvent.id();
        String lockKey = "lock:" + likeKey;
        String lockValue = UUID.randomUUID().toString();
        try {
            boolean locked = Boolean.TRUE.equals(
                    redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS));
            if (!locked) {
                log.error("Could not acquire lock for feed: {}", likeKey);
                return;
            }
            addLikeToPost(likeKey, likeDataKey, likeEvent);
        } finally {
            String currentLockValue = redisTemplate.opsForValue().get(lockKey);
            if (lockValue.equals(currentLockValue)) {
                redisTemplate.delete(lockKey);
            }
        }
    }

    private void addLikeToPost(String postLikesKey, String likeDataKey, LikeEvent event) {
        redisTemplate.opsForHash().put(likeDataKey, "userId", event.authorId());
        redisTemplate.opsForHash().put(likeDataKey, "createdAt", event.createdAt().toString());
        redisTemplate.opsForSet().add(postLikesKey, String.valueOf(event.id()));
    }
}
