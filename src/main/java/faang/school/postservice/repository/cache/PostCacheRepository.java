package faang.school.postservice.repository.cache;

import faang.school.postservice.model.event.PostEvent;

import java.util.Set;

public interface PostCacheRepository {

    void cachePost(String key, PostEvent value);

    Set<PostEvent> getMembers(String key);

    void removeFromSet(String key, PostEvent value);
}
