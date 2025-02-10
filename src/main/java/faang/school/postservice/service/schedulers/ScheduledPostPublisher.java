package faang.school.postservice.service.schedulers;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "${post.schedule.scheduled-cron}")
    public void publishScheduledPosts() {
        postService.publishScheduledPosts();
    }
}