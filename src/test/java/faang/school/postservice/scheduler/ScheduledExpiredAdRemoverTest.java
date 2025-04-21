package faang.school.postservice.scheduler;

import faang.school.postservice.service.AdService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ScheduledExpiredAdRemoverTest {

    @Mock
    private AdService adService;

    @InjectMocks
    private ScheduledExpiredAdRemover scheduledExpiredAdRemover;

    @Test
    void runDailyAdCleanup() {

        scheduledExpiredAdRemover.runDailyAdCleanup();

        verify(adService, times(1)).cleanupExpiredAds();
    }

}