package faang.school.postservice.consumer;

import faang.school.postservice.dto.Post.PostCacheDto;
import faang.school.postservice.model.LikeEvent;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventConsumer {

    private final RedisPostRepository redisPostRepository;

    @KafkaListener(topics = "like_topic")
    public void likeEvent(LikeEvent event, Acknowledgment acknowledgment) {
        log.info("A like event has been received");
        PostCacheDto postCacheDto = redisPostRepository.findById(event.getPostId()).orElse(null);
    }
}
