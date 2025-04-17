package faang.school.postservice.publisher;

import faang.school.postservice.dto.publisher.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventPublisher {

    @Value("${spring.data.redis.channels.like_post}")
    private String topic;
//    private final ObjectMapper objectMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(LikeEvent event) {
//        try {
//            String text = objectMapper.writeValueAsString(message);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
        redisTemplate.convertAndSend(topic, event);
        log.info("Like event published: {}", event);
    }
}
