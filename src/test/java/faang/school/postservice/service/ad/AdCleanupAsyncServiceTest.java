package faang.school.postservice.service.ad;

import faang.school.postservice.repository.ad.AdRepository;
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
    private AdRepository adRepository;

    @InjectMocks
    private AdCleanupAsyncService adCleanupAsyncService;

    @Test
    @DisplayName("Удаляет рекламу")
    public void removeBatch_whenRemoveBatchCalled_thenExecutesAsync() {
        adCleanupAsyncService.removeBatch(anyList());

        verify(adRepository, times(1)).deleteAll(anyList());
    }
}
