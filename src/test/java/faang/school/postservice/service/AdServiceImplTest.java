package faang.school.postservice.service;

import faang.school.postservice.mapper.AdMapper;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.util.AdFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdServiceImplTest {
    private static final int NUM_CHUNKS = 3;
    private List<Ad> inputAdList;
    private List<Long> extractedIds;
    private List<List<Long>> expectedBatchIds;

    @Captor
    private ArgumentCaptor<List<Long>> idListCaptor;

    @Mock
    private AdRepository adRepository;

    @Mock
    private AdMapper adMapper;

    @Mock
    private AdCleanupTaskService adCleanupTaskService;

    private AdServiceImpl adService;

    @BeforeEach
    void setUp() {
        adService = new AdServiceImpl( NUM_CHUNKS, adRepository,adMapper, adCleanupTaskService);
        inputAdList = AdFactory.createTwentyAds();
        extractedIds = List.of(3L, 6L, 7L, 9L, 12L, 14L, 15L, 18L);
        expectedBatchIds = List.of(
                List.of(3L, 6L, 7L),
                List.of(9L, 12L, 14L),
                List.of(15L, 18L)
        );
    }

    @Test
    @DisplayName("Test Cleanup of Expired Ads with Valid Data and Correct Batching")
    void cleanupExpiredAdsShouldCorrectBatchingWhenValidData() {
        //Arrange
        when(adRepository.findAll()).thenReturn(inputAdList);
        when(adMapper.extractIdsFromAds(anyList())).thenReturn(extractedIds);

        //Act
        adService.cleanupExpiredAds();

        //Assert
        verify(adCleanupTaskService, times(3)).deleteExpiredAdsBatch(idListCaptor.capture());
        List<List<Long>> actualBatchIds = idListCaptor.getAllValues();
        assertEquals(expectedBatchIds.size(), actualBatchIds.size());
        assertEquals(expectedBatchIds,actualBatchIds);
    }

    @Test
    @DisplayName("Test Cleanup of Expired Ads When No Data (Empty List), No Batching")
    void cleanupExpiredAdsShouldNotBatchWhenNoData() {
        //Arrange
        when(adRepository.findAll()).thenReturn(List.of());
        when(adMapper.extractIdsFromAds(anyList())).thenReturn(List.of());

        //Act
        adService.cleanupExpiredAds();

        //Assert
        verify(adCleanupTaskService, never()).deleteExpiredAdsBatch(idListCaptor.capture());
        List<List<Long>> actualBatchIds = idListCaptor.getAllValues();
        assertEquals(0, actualBatchIds.size());
        assertEquals(List.of(),actualBatchIds);
    }
}