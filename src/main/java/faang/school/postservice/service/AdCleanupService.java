package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdCleanupService {
    private final AdRepository adRepository;

    @Transactional
    public void cleanupExpiredAds(int batchSize) {
        List<Ad> expiredAds = adRepository.findExpiredAd();
        if (expiredAds.isEmpty()) {
            log.info("No expired ads found");
            return;
        }
        log.info("Found {} expired ads. Processing in batches...", expiredAds.size());
        ListUtils.partition(expiredAds, batchSize)
                .forEach(this::removeBatch);
    }

    @Async("adCleanupExecutor")
    public void removeBatch(List<Ad> adIds) {
        adRepository.deleteAll(adIds);
    }
}
