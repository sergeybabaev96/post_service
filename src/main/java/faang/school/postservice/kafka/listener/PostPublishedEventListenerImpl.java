package faang.school.postservice.kafka.listener;

import faang.school.postservice.dto.kafka.PostPublishedEvent;
import faang.school.postservice.mapper.event.EventMapper;
import faang.school.postservice.service.post.PostPublishedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostPublishedEventListenerImpl implements KafkaEventListener {

    private final EventMapper<PostPublishedEvent> eventMapper;
    private final PostPublishedService postPublishedService;

    @Value("${feed.max-size}")
    private int maxFeedSize;

    @KafkaListener(topics = "${kafka.post.topic}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaManualAckListenerContainerFactory")
    @Override
    public void listen(@Payload String message, Acknowledgment ack) {
        try {
            PostPublishedEvent postPublishedEvent = eventMapper.mapMessageToEvent(message, PostPublishedEvent.class);
            log.info("Received post event: {}", postPublishedEvent);
            postPublishedService.addPostsToFeed(postPublishedEvent);
        } finally {
            ack.acknowledge();
        }
    }
}
