package faang.school.postservice.scheduler;

import faang.school.postservice.service.AdService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdService adService;

    @Scheduled(cron = "${ads.cleanup.cron}")
    public void runDailyAdCleanup() {
        adService.cleanupExpiredAds();
    }
}
