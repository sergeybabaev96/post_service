package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdCleanupServiceTest {
    @Mock
    private AdRepository adRepository;
    @Mock
    private AdCleanupAsyncService adCleanupAsyncService;

    @InjectMocks
    private AdCleanupService adCleanupService;

    @Test
    @DisplayName("Не должен удалять ничего, если нет просроченной рекламы")
    public void cleanupExpiredAds_WhenCleanupCalled_ThenDoNothing() {
        int batchSize = 100;
        when(adRepository.findExpiredAd()).thenReturn(Collections.emptyList());

        adCleanupService.cleanupExpiredAds(batchSize);

        verify(adCleanupAsyncService, never()).cleanupExpiredAdsAsync(anyList());
        verify(adRepository, times(1)).findExpiredAd();
    }

    @Test
    @DisplayName("Разбивает рекламу на батчи и удаляет ее")
    public void cleanupExpiredAds_WhenCleanupCalled_ThenDeleteInBatches() {
        int batchSize = 2;
        List<Ad> expiredAds = List.of(new Ad(), new Ad(), new Ad());
        when(adRepository.findExpiredAd()).thenReturn(expiredAds);

        adCleanupService.cleanupExpiredAds(batchSize);

        verify(adCleanupAsyncService, times(batchSize)).cleanupExpiredAdsAsync(anyList());
    }
}
