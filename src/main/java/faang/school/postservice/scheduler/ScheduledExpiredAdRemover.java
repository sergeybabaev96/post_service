package faang.school.postservice.scheduler;

import faang.school.postservice.service.AdCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdCleanupService adCleanupService;
    @Value("${ad-cleanup.scheduled.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${ad-cleanup.scheduled.cron}")
    public void deleteExpiredAdPosts() {
        adCleanupService.cleanupExpiredAds(batchSize);
    }
}
