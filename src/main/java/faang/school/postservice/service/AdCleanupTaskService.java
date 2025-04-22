package faang.school.postservice.service;

import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdCleanupTaskService {
    private final AdRepository adRepository;

    @Async("adRemoverPool")
    public void deleteExpiredAdsBatch(List<Long> ids) {
        log.info("Thread: {}, deleting: {}", Thread.currentThread().getName(), ids);
        adRepository.deleteAllById(ids);
    }
}
