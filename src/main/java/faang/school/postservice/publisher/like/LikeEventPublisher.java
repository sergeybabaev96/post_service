package faang.school.postservice.publisher.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.Channels;
import faang.school.postservice.dto.like.LikeEventDto;
import faang.school.postservice.publisher.AbstractEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisher extends AbstractEventPublisher<LikeEventDto> {
    private final Channels channels;

    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper objectMapper,
                                 Channels channels) {
        super(redisTemplate, objectMapper);
        this.channels = channels;
    }

    @Override
    public void publish(LikeEventDto event) {
        handleEvent(event, channels.getLikeEventChannel());
    }
}
