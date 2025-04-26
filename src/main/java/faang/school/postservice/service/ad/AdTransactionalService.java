package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdTransactionalService {
    private final AdRepository adRepository;

    @Transactional
    public void deleteAdsBatchInTransaction(List<Ad> ads) {
        adRepository.deleteAll(ads);
        log.info("Successfully deleted {} ads", ads.size());
    }
}
