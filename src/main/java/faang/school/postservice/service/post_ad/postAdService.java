package faang.school.postservice.service.post_ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class postAdService {
    private final AdRepository adRepository;

    @Transactional(readOnly = true)
    public List<Long> findExpiredAds() {
        LocalDateTime now = LocalDateTime.now();
        List<Ad> expiredAds = adRepository.findExpiredAds(now);
        return expiredAds.stream()
                .map(Ad::getId)
                .collect(Collectors.toList());
    }

    @Async
    @Transactional
    public CompletableFuture<Void> deleteAdsBatch(List<Long> adIds) {
        log.debug("Starting deletion of ads batch, size: {}", adIds.size());
        adRepository.deleteExpiredAds(adIds);

        log.debug("Successfully deleted ads batch, size: {}", adIds.size());
        return CompletableFuture.completedFuture(null);
    }
}
