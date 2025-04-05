package faang.school.postservice.kafka;

import faang.school.postservice.repository.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import faang.school.postservice.dto.Post.PostEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final RedisFeedRepository feedRepository;

    @KafkaListener(
            topics = "${spring.kafka.topics.post.name}",
            containerFactory = "postEventFactory"
    )
    public void listenPostPublications(PostEvent postEvent, Acknowledgment acknowledgment) {
        try {
            feedRepository.addPostsToFollowersFeed(postEvent.getPostId(), postEvent.getSubscribersId());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing event {}, {}", postEvent, e.toString());
        }
    }
}
