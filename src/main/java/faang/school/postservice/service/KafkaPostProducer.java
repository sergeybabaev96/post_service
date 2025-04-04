package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.PostCreatedEvent;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        List<Long> allSubscribers = fetchSubscriberIds(post.getAuthorId());
        List<List<Long>> batches = partitionSubscriberIds(allSubscribers);
        int totalBatches = batches.size();

        for (int currentBatch = 0; currentBatch < totalBatches; currentBatch++) {
            List<Long> subscriberBatch = batches.get(currentBatch);
            boolean isLastBatch = currentBatch == totalBatches - 1;

            PostCreatedEvent event = createPostCreatedEvent(post, subscriberBatch, currentBatch + 1, totalBatches, isLastBatch);
            sendEvent(event);
        }
    }

    private List<Long> fetchSubscriberIds(Long authorId) {
        List<Long> allSubscribers = new ArrayList<>();
        int page = 0;

        while (true) {
            Pageable pageable = PageRequest.of(page, batchSize);
            List<Long> batch = postRepository.findAuthorSubscribers(authorId, pageable);
            if (batch.isEmpty()) {
                break;
            }
            allSubscribers.addAll(batch);
            page++;
        }

        return allSubscribers;
    }

    private List<List<Long>> partitionSubscriberIds(List<Long> allSubscribers) {
        return ListUtils.partition(allSubscribers, batchSize);
    }

    private PostCreatedEvent createPostCreatedEvent(Post post, List<Long> subscriberBatch, int currentBatch, int totalBatches, boolean isLastBatch) {
        return new PostCreatedEvent(
                post.getId(),
                post.getAuthorId(),
                subscriberBatch,
                currentBatch,
                totalBatches,
                isLastBatch
        );
    }

    private void sendEvent(PostCreatedEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, eventJson);
            log.info("Published post creation batch: postId={}, authorId={}, batchSize={}, batchNumber={}",
                    event.postId(), event.authorId(), event.subscriberIds().size(), event.batchNumber());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize post event batch: {}", e.getMessage());
            throw new RuntimeException("Failed to publish post creation event batch", e);
        }
    }
}
