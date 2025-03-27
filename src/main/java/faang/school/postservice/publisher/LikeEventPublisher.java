package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import faang.school.postservice.dto.like.LikePostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventPublisher {
    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private final ChannelTopic topic;
    private final ObjectMapper objectMapper;

    private String LOG_PUBLISHING_MESSAGE = "publishing message {}";

    public void publish(LikePostEvent likePostEvent) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(likePostEvent);
            redisTemplate.convertAndSend("like_topic", json);
            log.info(LOG_PUBLISHING_MESSAGE, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
