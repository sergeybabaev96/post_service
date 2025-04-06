package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdCleanupAsyncService {
    private final AdRepository adRepository;

    @Async("adCleanupExecutor")
    public void removeBatch(List<Ad> adIds) {
        adRepository.deleteAll(adIds);
    }
}