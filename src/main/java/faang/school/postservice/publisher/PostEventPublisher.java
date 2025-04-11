package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.EventDto;
import faang.school.postservice.properties.RedisConnectionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisConnectionProperties redisConnectionProperties;

    public void publish(EventDto event) {
        String topic = redisConnectionProperties.getTopic(RedisConnectionProperties.TopicKey.POST);
        event.setChannel(topic);
        log.info("Sending event {} to Redis in topic {}", event, topic);
        redisTemplate.convertAndSend(topic, event);
    }
}

