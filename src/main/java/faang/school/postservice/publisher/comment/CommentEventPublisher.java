package faang.school.postservice.publisher.comment;

import faang.school.postservice.event.comment.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentEventPublisher {

    private final KafkaTemplate<String, CommentEvent> kafkaTemplate;

    @Value("${spring.data.kafka.comments.channels}")
    private String commentsTopic;

    public void publishCommentEvent(CommentEvent event) {
        kafkaTemplate.send(commentsTopic, event);
    }
}
