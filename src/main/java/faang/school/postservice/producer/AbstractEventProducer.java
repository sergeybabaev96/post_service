package faang.school.postservice.producer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@AllArgsConstructor
public abstract class AbstractEventProducer<T> {
    protected final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEventToTopic(T event, String messageKey, String topicName) {
        kafkaTemplate.send(topicName, messageKey, event);
        log.info("Event {} sent successfully to topic {} with messageKey {}", event, topicName, messageKey);
    }

    public void sendEventToTopic(T event, String topicName) {
        kafkaTemplate.send(topicName, event);
        log.info("Event {} sent successfully to topic {}", event, topicName);
    }
}
