package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
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
public class CommentEventServiceImpl implements CommentEventService {
    private static final String FIELD_ID = "commentId";
    private static final String COMMENT_KEY_PREFIX = "post::comment:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${feed.comment.max-size}")
    private int commentMaxSize;

    @Override
    public void addCommentToPostToFeed(CommentEvent commentEvent) {
        String commentKey = COMMENT_KEY_PREFIX + commentEvent.postId();
        String lockKey = "lock:" + commentKey;
        String lockValue = UUID.randomUUID().toString();
        try {
            boolean locked = Boolean.TRUE.equals(
                    redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS));
            if (!locked) {
                log.error("Could not acquire lock for feed: {}", commentKey);
                return;
            }
            redisTemplate.opsForZSet().add(commentKey,
                    mapObjectToString(commentEvent), commentEvent.date().toEpochSecond(ZoneOffset.UTC));
            checkCommentDuplicate(commentEvent, commentKey);
            checkCommentsSize(commentKey);
        } finally {
            String currentLockValue = redisTemplate.opsForValue().get(lockKey);
            if (lockValue.equals(currentLockValue)) {
                redisTemplate.delete(lockKey);
            }
        }
    }

    private void checkCommentDuplicate(CommentEvent event, String feedKey) {
        Set<String> cachedComments = redisTemplate.opsForZSet().range(feedKey, 0, -1);
        if (cachedComments == null) {
            return;
        }
        List<String> forRemoveFromCache = cachedComments.stream()
                .filter(s -> {
                    String id = getFieldFromJson(s, FIELD_ID);
                    return id.equals(String.valueOf(event.commentId()));
                })
                .skip(1)
                .toList();
        if (!forRemoveFromCache.isEmpty()) {
            redisTemplate.opsForZSet().remove(feedKey, forRemoveFromCache.toArray());
        }
    }

    private void checkCommentsSize(String commentKey) {
        long size = Optional.ofNullable(redisTemplate.opsForZSet().size(commentKey)).orElse(0L);
        if (size > commentMaxSize) {
            redisTemplate.opsForZSet().removeRange(commentKey, 0, size - commentMaxSize - 1);
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
