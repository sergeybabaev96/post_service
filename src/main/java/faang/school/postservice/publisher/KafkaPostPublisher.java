package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.PostEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostPublisher extends AbstractEventPublisher<PostEvent> {

    private final NewTopic posts;

    public KafkaPostPublisher(KafkaTemplate<String, Object> kafkaTemplate, NewTopic posts) {
        super(kafkaTemplate);
        this.posts = posts;
    }

    public void sendEvent(PostEvent event) {
        super.sendEvent(event, posts);
    }
}
