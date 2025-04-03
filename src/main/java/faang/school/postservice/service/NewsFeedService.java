package faang.school.postservice.service;

import faang.school.postservice.config.props.CacheTtlProperties;
import faang.school.postservice.dto.feed.UserFeedDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.NewsFeedMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.CacheAuthor;
import faang.school.postservice.model.cache.CacheComment;
import faang.school.postservice.model.cache.CachePost;
import faang.school.postservice.repository.cache.CacheAuthorRepository;
import faang.school.postservice.repository.cache.CachePostRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static faang.school.postservice.model.cache.CacheAuthor.PROJECT_PREFIX;
import static faang.school.postservice.model.cache.CacheAuthor.USER_PREFIX;
import static faang.school.postservice.model.cache.CacheComment.COMMENT_PREFIX;

@Service
@RequiredArgsConstructor
public class NewsFeedService {
    private static final String FEED_PREFIX = "user_feed:";
    private static final int NEWS_FEED_RANGE_START = 0;
    private static final int FEED_END_INDEX = 1;
    private static final String LIKE_PREFIX = "post_likes:";

    private final CachePostRepository cachePostRepository;
    private final NewsFeedMapper newsFeedMapper;
    private final CacheAuthorRepository cacheAuthorRepository;
    private final UserService userService;
    private final CacheTtlProperties cacheTTLProperties;
    private final ProjectService projectService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostService postService;

    @Value("${news-feed.max-count-visible-comments:3}")
    private int maxComments;

    @Value("${news-feed.max-posts-in-feed:500}")
    private int maxPostsInFeed;

    public List<UserFeedDto> getUserFeed(long userId, Long lastPostId, int size) {
        var postsIds = getUserFeedPostsIds(userId, lastPostId, size);
        List<CachePost> cachePosts = cachePostRepository.findAllById(postsIds);
        checkAndAddMissingPosts(cachePosts, postsIds);
        return cachePosts.stream()
                .map(this::mapCachePostToFeedDto)
                .toList();
    }

    public void cacheCommentForPost(Comment comment) {
        if (!cachePostRepository.existsById(comment.getPost().getId())) {
            cachePost(comment.getPost());
        }
        CacheComment cacheComment = newsFeedMapper.toCache(comment);
        cacheComment.setAuthorId(cacheUserAsAuthor(comment.getAuthorId()).getId());
        String cacheKey = COMMENT_PREFIX + comment.getPost().getId();

        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();

                operations.opsForList().leftPush(cacheKey, cacheComment);
                operations.opsForList().trim(cacheKey, 0, maxComments - 1);

                return operations.exec();
            }
        });
    }

    public CachePost cachePost(Post post) {
        var optionalPost = cachePostRepository.findById(post.getId());
        if (optionalPost.isPresent()) {
            return optionalPost.get();
        }
        CachePost cachePost = newsFeedMapper.toCache(post, cacheTTLProperties.getPost().toMillis());
        cachePost.setAuthorId(cachePostAuthor(post).getId());

        return cachePostRepository.save(cachePost);
    }

    public CacheAuthor cacheUserAsAuthor(long userId) {
        return cacheAuthor(
                USER_PREFIX + userId,
                () -> newsFeedMapper.toCache(
                        userService.getUserDtoById(userId),
                        cacheTTLProperties.getAuthor().toMillis()
                ));
    }

    public CacheAuthor cacheProjectAsAuthor(long projectId) {
        return cacheAuthor(
                PROJECT_PREFIX + projectId,
                () -> newsFeedMapper.toCache(
                        projectService.getProjectById(projectId),
                        cacheTTLProperties.getAuthor().toMillis()
                ));
    }

    public void addPostForNewsFeed(Post post, List<Long> followersIds) {
        long postId = post.getId();
        long timestamp = post.getPublishedAt().toEpochSecond(ZoneOffset.UTC);
        followersIds.parallelStream()
                .forEach(id -> {
                    var cacheKey = FEED_PREFIX + id;
                    redisTemplate.opsForZSet().add(cacheKey, postId, timestamp);
                    redisTemplate.opsForZSet().removeRange(
                            cacheKey, NEWS_FEED_RANGE_START, -maxPostsInFeed - FEED_END_INDEX
                    );
                });
    }

    public void addLikeToPost(long postId) {
        redisTemplate.opsForValue()
                .increment(LIKE_PREFIX + postId, 1);
    }

    private UserFeedDto mapCachePostToFeedDto(CachePost post) {
        long postId = post.getId();
        var author = cacheAuthorRepository.findById(post.getAuthorId()).orElseGet(() ->
                cachePostAuthor(postService.getPostById(postId))
        );
        var postLikesCount = (Integer) redisTemplate.opsForValue()
                .get(LIKE_PREFIX + postId);
        var comments = redisTemplate.opsForList()
                .range(COMMENT_PREFIX + postId, 0, -1)
                .stream()
                .map(comment -> (CacheComment) comment)
                .toList();
        return newsFeedMapper.toDto(post, postLikesCount, author, comments);
    }

    private List<Long> getUserFeedPostsIds(long userId, Long lastPostId, int size) {
        String cacheKey = FEED_PREFIX + userId;
        Set<Object> objectsSet;

        Long index;

        if (lastPostId == null) {
            index = 0L;
        } else {
            index = redisTemplate.opsForZSet().reverseRank(cacheKey, lastPostId);
            if (index == null) {
                throw new EntityNotFoundException("Post not found");
            }
        }

        objectsSet = redisTemplate.opsForZSet()
                .reverseRange(cacheKey, index, index + size - 1);

        return objectsSet
                .stream()
                .map(id -> ((Number) id).longValue())
                .toList();
    }

    private void checkAndAddMissingPosts(List<CachePost> cachePosts, List<Long> postsIds) {
        Set<Long> foundPostIds = cachePosts.stream().map(CachePost::getId).collect(Collectors.toSet());
        Set<Long> missingPostIds = postsIds.stream()
                .filter(id -> !foundPostIds.contains(id))
                .collect(Collectors.toSet());

        if (!missingPostIds.isEmpty()) {
            missingPostIds.forEach(id -> {
                Post post = postService.getPostById(id);
                cachePosts.add(cachePost(post));
            });
        }
    }

    private CacheAuthor cachePostAuthor(Post post) {
        if (post.getAuthorId() != null) {
            return cacheUserAsAuthor(post.getAuthorId());

        } else {
            return cacheUserAsAuthor(post.getProjectId());
        }
    }

    private CacheAuthor cacheAuthor(String cacheAuthorId, Supplier<CacheAuthor> cacheAuthorSupplier) {
        var optionalAuthor = cacheAuthorRepository.findById(cacheAuthorId);
        return optionalAuthor
                .orElseGet(() -> cacheAuthorRepository.save(cacheAuthorSupplier.get()));
    }
}
