package faang.school.postservice.service;

import faang.school.postservice.mapper.AdMapper;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AdServiceImpl implements AdService {

    private final int numChunks;
    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final AdCleanupTaskService adCleanupTaskService;


    @Autowired
    public AdServiceImpl(@Value("${ads.cleanup.chunk-size}") int numChunk, AdRepository adRepository,
                         AdMapper adMapper, AdCleanupTaskService adCleanupTaskService) {
        this.numChunks = numChunk;
        this.adRepository = adRepository;
        this.adMapper = adMapper;
        this.adCleanupTaskService = adCleanupTaskService;
    }

    @Override
    public void cleanupExpiredAds() {
        List<Long> allExpiredAdIds = adMapper.extractIdsFromAds(getExpiredAds());
        int totalSize = allExpiredAdIds.size();
        int batch = (int) Math.ceil((double) totalSize / numChunks);
        for (int startIndex = 0; startIndex < totalSize; startIndex += batch) {
            int endIndex = Math.min(startIndex + batch, totalSize);
            adCleanupTaskService.deleteExpiredAdsBatch(allExpiredAdIds.subList(startIndex,endIndex));
        }

    }

    private List<Ad> getExpiredAds() {
        Iterable<Ad> allAds = adRepository.findAll();
        List<Ad> adList = StreamSupport.stream(allAds.spliterator(), false).toList();
        List<Ad> filteredList = adList.stream()
                .filter(ad -> ad.getAppearancesLeft() <= 0 || ad.getEndDate().isBefore(LocalDateTime.now()))
                .toList();
        return filteredList;
    }


}
