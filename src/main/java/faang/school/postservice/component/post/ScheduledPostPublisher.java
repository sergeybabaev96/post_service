package faang.school.postservice.component.post;

import faang.school.postservice.service.post.interfaces.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "0 * * * * ?")
    public void startPublishPost() {
        postService.publishScheduledPosts()
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Failed to publish scheduled posts", throwable);
                    } else {
                        log.info("All posts published successfully");
                    }
                });
    }
}