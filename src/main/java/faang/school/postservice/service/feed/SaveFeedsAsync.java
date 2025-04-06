package faang.school.postservice.service.feed;

import faang.school.postservice.event.post.PostCreatedEvent;
import faang.school.postservice.repository.NewsFeedJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaveFeedsAsync {

    private final NewsFeedJdbcRepository newsFeedJdbcRepository;
    @Async
    public void saveFeedsToDb(PostCreatedEvent event) {
        Map<Long, List<Long>> feeds = event.getFollowerIds().stream()
                .collect(Collectors.toMap(userId -> userId,
                        userId -> List.of(event.getPostId())));

        newsFeedJdbcRepository.batchInsertFeeds(feeds);
    }
}
