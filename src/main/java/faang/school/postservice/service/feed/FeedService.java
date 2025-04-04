package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.FeedResponse;

public interface FeedService {

    FeedResponse getNewsFeed(Long postId);

}
