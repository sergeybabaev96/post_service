package faang.school.postservice.kafka.listener;

import faang.school.postservice.dto.kafka.LikeEvent;
import faang.school.postservice.mapper.event.EventMapper;
import faang.school.postservice.service.event.LikeEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaLikeEventListener implements KafkaEventListener {

    private final EventMapper<LikeEvent> eventMapper;
    private final LikeEventService likeEventService;

    @KafkaListener(topics = "${kafka.like.topic}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaManualAckListenerContainerFactory")
    @Override
    public void listen(String message, Acknowledgment ack) {
        LikeEvent likeEvent = eventMapper.mapMessageToEvent(message, LikeEvent.class);
        try {
            log.info("Received post event: {}", likeEvent);
            likeEventService.addLikeToPost(likeEvent);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Failed add like with id = %d to post with id = %d",
                    likeEvent.id(), likeEvent.postId()));
        } finally {
            ack.acknowledge();
        }
    }
}