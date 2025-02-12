package faang.school.postservice.kafka;

import faang.school.postservice.model.LikeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LikeEventPublisher implements EventPublisher<LikeEvent> {
    private final KafkaTemplate<String, LikeEvent> kafkaTemplate;
    private final NewTopic likeTopic;

    public LikeEventPublisher(KafkaTemplate<String, LikeEvent> kafkaTemplate,
                              @Qualifier("likeTopic") NewTopic likeTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.likeTopic = likeTopic;
    }

    @Override
    public void publish(LikeEvent message) {
        kafkaTemplate.send(likeTopic.name(), message);
        log.info("\n\n\n\n\t\t\t\t\tSent to {} {}\n\n\n\n\n\n\n\n",
                likeTopic.name(), message);
    }
}
