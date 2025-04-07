package faang.school.postservice.service.ad;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
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

    @Test
    @DisplayName("При попытке удалить рекламу выбрасывается RuntimeException")
    public void givenExceptionDuringAdDeletion_whenCleanupCalled_thenRuntimeExceptionIsThrown() {
        doThrow(new RuntimeException())
                .when(adTransactionalService).deleteAdsBatchInTransaction(anyList());
        Exception exception = assertThrows(RuntimeException.class,
                () -> adCleanupAsyncService.cleanupExpiredAdsAsync(anyList()));
        assertEquals("Async cleanup failed for batch", exception.getMessage());
    }
}
