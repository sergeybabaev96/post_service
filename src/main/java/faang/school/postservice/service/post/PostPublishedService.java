package faang.school.postservice.service.post;

import faang.school.postservice.dto.kafka.PostPublishedEvent;

public interface PostPublishedService {

    void addPostsToFeed(PostPublishedEvent event);

}
