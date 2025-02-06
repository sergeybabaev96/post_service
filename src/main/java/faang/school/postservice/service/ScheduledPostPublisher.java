package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "${post.scheduled-cron}")
    public void publishScheduledPosts() {
        postService.publishScheduledPosts();
    }
}