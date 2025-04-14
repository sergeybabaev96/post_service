package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.EventDto;
import faang.school.postservice.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    public void publish(EventDto event) {
        String topic = redisProperties.getTopic(event.getEventType());
        redisTemplate.convertAndSend(topic, event);
        log.info("Send event {} to Redis in topic {}", event, topic);
    }
}

