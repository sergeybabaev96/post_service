package faang.school.postservice.service;

import faang.school.postservice.schedulers.AdsExpirationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdsService {
    private final AdsExpirationManager adsExpirationManager;

    public void deleteExpiredAds() {
        adsExpirationManager.deleteExpiredAds();
    }
}
