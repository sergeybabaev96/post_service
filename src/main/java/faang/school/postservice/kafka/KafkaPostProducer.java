package faang.school.postservice.kafka;

import faang.school.postservice.model.event.PostBySubscribersEvent;
import faang.school.postservice.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPostProducer {
    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, PostBySubscribersEvent> kafkaTemplate;

    public void sendMessage(PostBySubscribersEvent message) {
        String postTopic = kafkaProperties.getPostTopic();
        kafkaTemplate.send(postTopic, message);
        log.info("Message sent to topic: {}, message: {}", postTopic, message);
    }
}
