package faang.school.postservice.service;

import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.schedulers.AdsExpirationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdsService {
    private final AdsExpirationManager adsExpirationManager;
    private final AdRepository adRepository;

    public void deleteExpiredAds() {
        adsExpirationManager.deleteExpiredAds();
    }
}
