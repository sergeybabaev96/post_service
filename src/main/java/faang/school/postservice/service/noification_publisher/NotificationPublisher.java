package faang.school.postservice.service.noification_publisher;

import faang.school.postservice.notification.PostNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPublisher {
    private static final String CHANNEL = "post-notifications";

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(PostNotification notification) {
        try {
            log.info("Publishing notification: {}", notification);
            redisTemplate.convertAndSend(CHANNEL, notification);
        } catch (Exception e) {
            log.error("Failed to publish notification: {}", notification, e);
            throw new RuntimeException("Failed to publish notification", e);
        }
    }
}
