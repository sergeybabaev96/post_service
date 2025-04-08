package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Component
public class ModerationScheduler {
    private final PostService postService;
    private final ExecutorService moderationExecutor;


    @Scheduled(cron = "${moderation.job.cron}")
    public void runModerationJob() {
        postService.moderatePosts();
    }

    @PreDestroy
    public void shutdown() {
        moderationExecutor.shutdown();
        try {
            if (!moderationExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                moderationExecutor.shutdownNow();
                log.info("Executor did not terminate gracefully");
            }
        } catch (InterruptedException e) {
            moderationExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
