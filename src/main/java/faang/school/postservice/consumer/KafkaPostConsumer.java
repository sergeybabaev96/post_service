package faang.school.postservice.consumer;

import faang.school.postservice.dto.feed.FeedPostDeleteEvent;
import faang.school.postservice.dto.feed.FeedPostEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@DependsOn("kafkaAdmin")
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final FeedService feedService;

    @KafkaListener(topics = {"${spring.data.kafka.topics.post.name}"},
            groupId = "${spring.data.kafka.consumer.groups.post}")
    public void consume(FeedPostEvent event) {
        Long postId = event.getPostId();
        log.info("Received PostEvent for post ID: {}", postId);

        try {
            feedService.addPostToFeed(event.getSubscribersIds(), postId, event.getPublishedAt());
            log.info("Successfully processed FeedPostEvent for post ID: {}", postId);
        } catch (Exception e) {
            log.error("Failed to process FeedPostEvent for post ID: {}", postId, e);
        }
    }

    @KafkaListener(
            topics = "${spring.data.kafka.topics.delete-post.name}",
            groupId = "${spring.data.kafka.consumer.groups.post}"
    )
    public void consumeDelete(FeedPostDeleteEvent deleteEvent) {
        Long postId = deleteEvent.getPostId();
        log.info("Received FeedPostDeleteEvent for post ID: {}", postId);

        try {
            feedService.handlePostDeletion(postId);
            log.info("Successfully handled deletion for post ID: {}", postId);
        } catch (Exception e) {
            log.error("Failed to handle deletion for post ID: {}", postId, e);
        }
    }
}
