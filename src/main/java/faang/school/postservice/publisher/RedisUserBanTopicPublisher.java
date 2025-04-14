package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUserBanTopicPublisher {

    @Qualifier("userBanRedisTemplate")
    private final RedisTemplate<String, Long> redisTemplate;
    @Qualifier("userBanTopic")
    private final ChannelTopic topic;

    public void publish(Long userIdToBan) {
        redisTemplate.convertAndSend(topic.getTopic(), userIdToBan);
    }
}
