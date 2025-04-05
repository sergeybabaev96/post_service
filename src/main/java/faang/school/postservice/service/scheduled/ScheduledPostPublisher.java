package faang.school.postservice.service.scheduled;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "0 * * * * *")
    public void publishSchedulePosts(){
        log.info("Публикуем запланированные посты: {}", java.time.LocalDateTime.now());
        postService.publishScheduledPosts();
    }
}
