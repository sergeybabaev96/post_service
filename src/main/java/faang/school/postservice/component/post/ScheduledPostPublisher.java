package faang.school.postservice.component.post;

import faang.school.postservice.service.post.interfaces.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ScheduledPostPublisher {
    PostService postService;

    @Scheduled(cron = "0 * * * * ?")
    void startPublishPost() {
        try {
            postService.publishScheduledPosts();
        } catch (Exception e) {
            log.error("Failed to publish scheduled posts", e);
        }
    }
}