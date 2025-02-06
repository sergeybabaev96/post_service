package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.properties.post.PostProperties;
import faang.school.postservice.properties.user.UserBanProperties;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserBanProperties userBanProperties;
    private final PostProperties postProperties;
    private final RedisPublisher redisPublisher;

    @Override
    public void banUsersWithManyUnverifiedPosts() {
        Map<Long, List<Post>> unverifiedPostsByUsers = postRepository.findByVerified(false).stream()
                .collect(groupingBy(Post::getAuthorId));
        unverifiedPostsByUsers.entrySet().stream()
                .filter(entry -> entry.getValue().size() > postProperties.getMaxUnverified())
                .forEach(entry -> {
                    long authorId = entry.getKey();
                    redisPublisher.publish(userBanProperties.getChannel(), String.valueOf(authorId));
                    log.info("Sent ban request for author: {}", authorId);
                });
    }
}
