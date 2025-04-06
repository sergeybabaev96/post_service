package faang.school.postservice.scheduler;

import faang.school.postservice.service.AdCleanupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ScheduledExpiredAdRemoverTest {
    @Mock
    private AdCleanupService adCleanupService;

    @InjectMocks
    private ScheduledExpiredAdRemover scheduledExpiredAdRemover;

    @Test
    @DisplayName("Вызывает метод для удаление просроченной рекламы")
    public void deleteExpiredAdPosts_WhenCleanupCalled_ThenDeleteExpiredAdPosts() {
        scheduledExpiredAdRemover.deleteExpiredAdPosts();

        verify(adCleanupService).cleanupExpiredAds(anyInt());
    }
}
