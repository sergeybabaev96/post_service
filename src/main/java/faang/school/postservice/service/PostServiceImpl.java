package faang.school.postservice.service;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
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

    @Value("${app.moderation.post-count-threshold}")
    private int postCountThreshold;

    @Override
    public Post findPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException(String.format("Post with id %s not found", postId)));
    }

    @Override
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
