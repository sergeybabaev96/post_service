package faang.school.postservice.producer;

import faang.school.postservice.dto.feed.FeedPostDeleteEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostDeleteProducer extends AbstractEventProducer<FeedPostDeleteEvent> {
    public KafkaPostDeleteProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }
}
