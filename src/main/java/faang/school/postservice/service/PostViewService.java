package faang.school.postservice.service;

import faang.school.postservice.dto.kafka.PostViewsEvent;

public interface PostViewService {

    void addViewToPost(PostViewsEvent postViewEvent);
}
