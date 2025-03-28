package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostCreatedAsyncService {
    private final UserServiceClient userServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.post}")
    String topicName;

    @Async
    public void processPostCreated(Post result) {
        List<Long> subscribersIds = userServiceClient.getFollowerIds(result.getAuthorId());
        PostCreatedEvent createdEvent = new PostCreatedEvent(
                result.getId(), result.getAuthorId(), subscribersIds
        );
        kafkaTemplate.send(topicName, result.getId().toString(), createdEvent);
    }
}
