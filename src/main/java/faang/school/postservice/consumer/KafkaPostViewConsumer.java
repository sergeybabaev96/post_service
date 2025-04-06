package faang.school.postservice.consumer;

import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewConsumer {

    private final PostService postService;

    @KafkaListener(topics = "post.views", groupId = "post-view-group", containerFactory = "postViewKafkaListenerContainerFactory")
    public void listen(PostViewEvent event, Acknowledgment ack) {
        try {
            postService.incrementView(event.postId());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Произошла ошибка при увеличении просмотров у поста", e);
        }
    }
}
