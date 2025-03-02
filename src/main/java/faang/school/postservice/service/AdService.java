package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final AsyncDeleteService asyncDeleteService;

    @Transactional
    public void removeExpiredAds(int batchSize) {
        List<Ad> expiredAds = adRepository.findAll().stream()
                .filter(ad -> ad.getEndDate().isBefore(LocalDateTime.now()) || ad.getAppearancesLeft() == 0)
                .toList();

        ListUtils.partition(expiredAds, batchSize)
                .forEach(asyncDeleteService::deleteExpiredBatch);
    }
}