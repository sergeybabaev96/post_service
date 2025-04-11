package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для асинхронного удаления устаревшей рекламы.
 * Использует отдельный потоковый пул ("adCleanupExecutor") для выполнения операций удаления,
 * чтобы не блокировать основной поток приложения.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdCleanupAsyncService {
    private final AdTransactionalService adTransactionalService;

    /**
     * Асинхронно удаляет переданный список рекламы.
     *
     * @param expiredAds список рекламы для удаления
     */
    @Async("adCleanupExecutor")
    public void cleanupExpiredAdsAsync(List<Ad> expiredAds) {
        try {
            adTransactionalService.deleteAdsBatchInTransaction(expiredAds);
        } catch (Exception e) {
            log.error("Cleanup failed for {} ads. Error: {}",
                    expiredAds.size(),
                    e.getMessage(),
                    e
            );
        }
    }
}