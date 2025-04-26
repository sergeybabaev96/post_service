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
public class AdTransactionalServiceTest {
    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdTransactionalService adTransactionalService;

    @Test
    @DisplayName("Удаляет рекламу транзакциями")
    public void givenAdsToDelete_WhenDeleteAdsBatchInTransaction_ThenAdsAreRemoved() {
        adTransactionalService.deleteAdsBatchInTransaction(anyList());

        verify(adRepository, times(1)).deleteAll(anyList());
    }
}
