package faang.school.postservice.producer;

import faang.school.postservice.dto.post.PostViewEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class KafkaPostViewProducer implements KafkaEventProducer<PostViewEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final NewTopic postViewTopic;

    public KafkaPostViewProducer(KafkaTemplate<String, Object> kafkaTemplate, @Qualifier(value = "postViews") NewTopic postViewTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.postViewTopic = postViewTopic;
    }

    @Override
    public void send(PostViewEvent event) {
        kafkaTemplate.send(postViewTopic.name(), event);
        log.info("Post_view event was sent to Kafka topic: {}", postViewTopic.name());
    }
}
