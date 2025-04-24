package faang.school.postservice.service.post_check;

import faang.school.postservice.service.post.interfaces.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostsScheduledChecker {
    private final PostService postService;

    @Scheduled(cron = "${scheduler.post-correction.cron}")
    public void correctPostsDaily() {
        postService.correctUnpublishedPosts();
    }
}