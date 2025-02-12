package faang.school.postservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.like-events}")
    private String topicName;

    public void publishLikeEvent(LikeEvent event) {
        kafkaTemplate.send(topicName, event);
    }
}