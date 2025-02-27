package faang.school.postservice.service.ad;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AdService {

    List<Long> findExpiredAds();

    void deleteAds(List<Long> ads);

    List<Long> deleteExpiredAds();
}