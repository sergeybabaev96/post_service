package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.PostCreatedEvent;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Setter
public class KafkaPostProducer {
    private final PostRepository postRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.publish-post-topic.name}")
    private String topic;

    @Value("${spring.kafka.topics.publish-post-topic.subscribers-batch-size:1000}")
    private int batchSize;

    public void publishPostCreationEvent(Post post) {
        List<Long> allSubscribers = postRepository.findAllAuthorSubscribers(post.getAuthorId());
        int totalBatches = (int) Math.ceil((double) allSubscribers.size() / batchSize);

        for (int i = 0; i < allSubscribers.size(); i += batchSize) {
            List<Long> subscriberBatch = allSubscribers.subList(
                    i, Math.min(i + batchSize, allSubscribers.size()));

            int currentBatch = (i / batchSize) + 1;
            boolean isLastBatch = currentBatch == totalBatches;

            PostCreatedEvent event = new PostCreatedEvent(
                    post.getId(),
                    post.getAuthorId(),
                    subscriberBatch,
                    currentBatch,
                    totalBatches,
                    isLastBatch
            );

            try {
                String eventJson = objectMapper.writeValueAsString(event);
                kafkaTemplate.send(topic, eventJson);
                log.info("Published post creation batch: postId={}, authorId={}, batchSize={}, offset={}",
                        post.getId(), post.getAuthorId(), subscriberBatch.size(), i);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize post event batch: {}", e.getMessage());
                throw new RuntimeException("Failed to publish post creation event batch", e);
            }
        }
    }
}
