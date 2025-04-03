package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.repository.cache.RedisFeedRepository;
import faang.school.postservice.repository.cache.RedisPostRepository;
import faang.school.postservice.repository.cache.RedisUserRepository;
import faang.school.postservice.service.cache.RedisCacheService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final RedisFeedRepository redisFeedRepository;
    private final RedisCacheService redisCacheService;
    private final UserServiceClient userServiceClient;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final PostService postService;

    @Value("${spring.data.redis.feed-cache.batch-size}")
    private int FEED_BATCH_SIZE;

    @Value("${spring.data.redis.feed-cache.default-post-id}")
    private long DEFAULT_POST_ID;

    public void addPostToAuthorSubscribers(long postId, List<Long> subscribersId) {
        subscribersId.forEach(subscriberId -> addPostToSubscriberFeed(postId, subscriberId));
    }

    public List<PostReadDto> getUserFeed(long userId, Long postId) {
        List<Long> postsId = getPostsIdBatch(userId, postId);

        if (postId == null) {
            postId = DEFAULT_POST_ID;
        }

        List<PostReadDto> postsDto;
        if (!postsId.isEmpty()) {
            postsDto = getPosts(postsId);
        } else {
            postsDto = getPostsIfFollowerPostsIdEmpty(userId, postId);
        }
        return postsDto;
    }

    private List<PostReadDto> getPosts(List<Long> postsId) {
        return redisCacheService.getPostCacheByIds(postsId);
    }

    private List<PostReadDto> getPostsIfFollowerPostsIdEmpty(long userId, long postId) {
        UserCache userCache = getUserCache(userId);
        List<PostReadDto> postsByAuthorIds = postService
                .getPostsByAuthorIds(userCache.getSubscribersId(), postId, FEED_BATCH_SIZE);

        redisFeedRepository.saveAll(userCache.getUserId(), postsByAuthorIds);//метод не дописан

        redisPostRepository.saveAll(postsByAuthorIds.stream()
                .map(postMapper::toPostCache)
                .peek(redisCacheService::initVersion)
                .toList());

        return postsByAuthorIds;
    }

    private UserCache getUserCache(long userId) {
        return redisUserRepository.findById(userId)
                .orElseGet(() -> redisUserRepository
                        .save(userMapper.toUserCache(userServiceClient.getUser(userId))));
    }

    private List<Long> getPostsIdBatch(long userId, Long postId) {
        Set<Object> posts;
        if (redisFeedRepository.findPostsId(userId).isEmpty()) {
            return emptyList();
        }

        if (postId != null) {
            Long rank = redisFeedRepository.getRank(userId, postId);
            posts = redisFeedRepository.getRange(userId, rank + 1, rank + FEED_BATCH_SIZE);
        } else {
            posts = redisFeedRepository.getRange(userId, 0, FEED_BATCH_SIZE - 1);
        }

        return convertObjectsToLongs(posts);
    }

    private List<Long> convertObjectsToLongs(Set<Object> posts) {
        return posts.stream()
                .map(Object::toString)
                .map(Long::valueOf)
                .toList();
    }

    private void addPostToSubscriberFeed(long postId, long subscriberId) {
        Set<Long> postsId = redisFeedRepository.findPostsId(subscriberId);
        redisFeedRepository.checkMaxFeedSize(postsId, subscriberId);
        redisFeedRepository.add(subscriberId, postId);
    }
}
