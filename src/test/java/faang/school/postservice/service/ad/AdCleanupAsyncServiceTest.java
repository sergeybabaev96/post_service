package faang.school.postservice.service.ad;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AdCleanupAsyncServiceTest {
    @Mock
    private AdTransactionalService adTransactionalService;

    @InjectMocks
    private AdCleanupAsyncService adCleanupAsyncService;

    @Test
    @DisplayName("Успешное удаление рекламы")
    public void givenExpiredAds_WhenCleanupCalled_ThenAdsAreDeleted() {
        adCleanupAsyncService.cleanupExpiredAdsAsync(anyList());

        verify(adTransactionalService, times(1)).deleteAdsBatchInTransaction(anyList());
    }
}
