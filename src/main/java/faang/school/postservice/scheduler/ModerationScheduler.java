package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class ModerationScheduler {
    private final PostService postService;
    // "0 0 2 * * ?"

    @Scheduled(cron = "${moderation.job.cron}")
    public void runModerationJob() {
        postService.moderatePosts();
    }
}
