package faang.school.postservice.kafka;

import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.service.RedisViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {

    private final RedisViewService redisViewService;

    @KafkaListener(topics = "post_views", groupId = "postServiceListenerGroup")
    public void consume(PostViewEvent event) {
        try {
            Long postId = event.getPostId();
            redisViewService.incrementViewCount(postId);
        } catch (Exception e) {
            log.error("Failed to process PostViewEvent: {}", event, e);
        }
    }
}