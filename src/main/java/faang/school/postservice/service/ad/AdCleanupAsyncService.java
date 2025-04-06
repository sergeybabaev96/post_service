package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
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
public class AdCleanupAsyncService {
    private final AdRepository adRepository;

    /**
     * Асинхронно удаляет переданный список рекламы.
     *
     * @param expiredAds список рекламы для удаления
     */
    @Async("adCleanupExecutor")
    public void removeBatch(List<Ad> expiredAds) {
        adRepository.deleteAll(expiredAds);
    }
}