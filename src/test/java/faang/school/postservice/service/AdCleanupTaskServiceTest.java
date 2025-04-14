package faang.school.postservice.service;

import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class AdCleanupTaskServiceTest {
    @Captor
    ArgumentCaptor<List<Long>> adIdsCaptor;

    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdCleanupTaskService adCleanupTaskService;

    @Test
    @DisplayName("Delete expired ad list when list has elements")
    void deleteExpiredAdsBatchShouldInvokeDeleteAllByIdMethod() throws InterruptedException {
        List<Long> adIds = List.of(1L, 2L, 3L);
        doNothing().when(adRepository).deleteAllById(adIds);

        adCleanupTaskService.deleteExpiredAdsBatch(adIds);

        Thread.sleep(1000);
        verify(adRepository, times(1)).deleteAllById(adIdsCaptor.capture());
        assertEquals(adIds, adIdsCaptor.getValue());
    }

}