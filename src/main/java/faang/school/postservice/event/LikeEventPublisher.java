package faang.school.postservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_NAME = "like-events";

    public void publishLikeEvent(LikeEvent event) {
        kafkaTemplate.send(TOPIC_NAME, event);
    }
}