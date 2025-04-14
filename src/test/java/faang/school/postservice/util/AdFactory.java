package faang.school.postservice.util;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdFactory {
    public static List<Ad> createTwentyAds() {
        List<Ad> ads = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Post post = Post.builder()
                    .id((long) i)
                    .content("Content of post " + i)
                    .authorId(100L + i)
                    .projectId(200L + i)
                    .published(true)
                    .publishedAt(LocalDateTime.now().minusDays(i))
                    .scheduledAt(LocalDateTime.now().minusDays(i + 1))
                    .deleted(false)
                    .createdAt(LocalDateTime.now().minusDays(i + 2))
                    .updatedAt(LocalDateTime.now().minusDays(i))
                    .build();

            boolean isExpired = i % 3 == 0;
            boolean isExhausted = i % 7 == 0;
            LocalDateTime startDate = LocalDateTime.now().minusDays(10);
            LocalDateTime endDate = isExpired
                    ? LocalDateTime.now().minusDays(1)
                    : LocalDateTime.now().plusDays(5);

            long appearancesLeft = isExhausted ? 0 : 10 + i;

            Ad ad = Ad.builder()
                    .id(i)
                    .post(post)
                    .buyerId(500L + i)
                    .appearancesLeft(appearancesLeft)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            ads.add(ad);
        }
        return ads;
    }
}
