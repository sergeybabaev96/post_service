package faang.school.postservice.component.post;

import faang.school.postservice.service.post.interfaces.PostService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ScheduledPostPublisher {
    PostService postService;

    @Scheduled(cron = "0 * * * * *")
    void startPublishPost() {

        postService.publishScheduledPosts();
    }
}