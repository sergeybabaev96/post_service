package faang.school.postservice.broker.consumer;

import faang.school.postservice.dto.post.PostPublicationEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostEventConsumer {

    private final FeedService feedService;
    //private final KafkaProperties kafkaProperties;
    private final AsyncTaskExecutor asyncTaskExecutor;

    //TODO сделать на основе properties
    @KafkaListener(topics = "posts", groupId = "newsfeed")
    public void consume(PostPublicationEvent postPublicationEvent, Acknowledgment acknowledgment) {

        CompletableFuture<Void> result = CompletableFuture.runAsync(() ->
                feedService.processNewPost(
                        postPublicationEvent.postId(),
                        postPublicationEvent.followersIds()),
                asyncTaskExecutor);

        result.whenComplete((res, exception) -> {
            if (exception != null) {
                log.error("Error consuming message with post id {}", postPublicationEvent.postId());
            }else {
                feedService.processNewPost(postPublicationEvent.postId(), postPublicationEvent.followersIds());
                log.info("### User {} is published the post {}",
                        postPublicationEvent.userId(), postPublicationEvent.postId());
                acknowledgment.acknowledge();
            }
        });

//        feedService.processNewPost(postPublicationEvent.postId(), postPublicationEvent.followersIds());
//        log.info("### User {} is published the post {}", postPublicationEvent.userId(), postPublicationEvent.postId());
    }
}
