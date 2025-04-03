package faang.school.postservice.service;

import faang.school.postservice.publisher.RedisUserBanTopicPublisher;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final RedisUserBanTopicPublisher redisUserBanTopicPublisher;

    @Value("${moderation.post-count-threshold}")
    private int postCountThreshold;

    public void banUsersIfRequired() {
        List<Long> authorIdsToBan;
        try {
            authorIdsToBan = postRepository.findAuthorIdsByUnverifiedPostsThreshold(postCountThreshold);
        } catch (DataAccessException ex) {
            throw new faang.school.postservice.exception.DataAccessException(ex.getMessage());
        }

        authorIdsToBan.forEach(redisUserBanTopicPublisher::publish);
    }
}
