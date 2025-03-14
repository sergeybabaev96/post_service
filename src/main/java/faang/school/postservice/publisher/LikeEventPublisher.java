package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.exception.LikeEventPublishingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisher {
    private final RedisTemplate<String, Long> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(LikeEvent likeEvent) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(likeEvent);
        } catch (JsonProcessingException e) {
            throw new LikeEventPublishingException("Failed to serialize LikeEvent to JSON");
        }
        redisTemplate.convertAndSend("like_topic", json);
    }
}
