package faang.school.postservice.scheduled;

import faang.school.postservice.service.post_ad.postAdService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final postAdService postAdService;

    //Настройки берутся напрямую из application.yml)
    @Value("${app.scheduler.ad.batch-size}")
    private int batchSize;
    @Value("${app.scheduler.postAd-removal-cron}")
    private String cronExpression;

    //запускаем каждый день в 2:00 (из application.yml)
    @Scheduled(cron = "#{cronExpression}")

    public void removeExpiredAds() {
        List<Long> expiredAds = postAdService.findExpiredAds();
        List<List<Long>> batches = partitionList(expiredAds, batchSize);
        batches.forEach(postAdService::deleteAdsBatch);
    }

    private static <T> List<List<T>> partitionList(List<T> list, int size) {
        if (list == null || size <= 0) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return result;
    }

}