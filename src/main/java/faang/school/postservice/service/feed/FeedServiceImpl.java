package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedResponse;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.service.cache.PostCacheService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    public static final String USER_FEED_PREFIX = "user:feed:";
    private final UserServiceClient userServiceClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;
    private final UserContext userContext;
    private final PostMapper postMapper;
    private final PostCacheService postCacheService;

    @Value("${feed.page-size:20}")
    private int pageSize;

    @Override
    public FeedResponse getNewsFeed(Long afterPostId) {
        long currentUserId = userContext.getUserId();
        String feedKey = USER_FEED_PREFIX + currentUserId;
        List<String> postIds = postCacheService.getPostIdsFromRedis(feedKey, afterPostId);
        if (postIds.size() < pageSize) {
            List<String> additionalPostIds = retrieveRemainingPostsFromDb(
                    String.valueOf(currentUserId), afterPostId, pageSize - postIds.size());
            postIds.addAll(additionalPostIds);
        }
        List<PostResponseDto> posts = enrichPostsData(postIds);
        return buildResponse(posts);
    }

    private List<String> retrieveRemainingPostsFromDb(String userId, long afterPostId, int limit) {
        List<PostResponseDto> posts = postRepository.findForUserFeed(Long.parseLong(userId), afterPostId, limit).stream()
                .map(postMapper::toDto)
                .toList();
        cachePosts(posts);
        return posts.stream()
                .map(post -> String.valueOf(post.id()))
                .toList();
    }

    private List<PostResponseDto> enrichPostsData(List<String> postIds) {
        return postIds.stream()
                .map(postId -> {
                    PostResponseDto postDto = getPostFromRedis(postId);
                    if (postDto == null) {
                        postDto = getPostFromDatabase(postId);
                    }
                    return postDto;
                })
                .toList();
    }

    private PostResponseDto getPostFromRedis(String postId) {
        String postKey = "post:" + postId;
        String authorKey = "user:" + postId;
        Map<Object, Object> postData = redisTemplate.opsForHash().entries(postKey);
        if (postData.isEmpty()) {
            return null;
        }
        Map<Object, Object> authorData = redisTemplate.opsForHash().entries(authorKey);
        if (authorData.isEmpty()) {
            return null;
        }
        return PostResponseDto.builder()
                .id(Long.parseLong(postId))
                .content((String) postData.get("content"))
                .createdAt(LocalDateTime.from(Instant.parse((String) postData.get("createdAt"))))
                .likesCount(Integer.parseInt((String) postData.get("likesCount")))
                .commentsCount(Integer.parseInt((String) postData.get("commentsCount")))
                .viewsCount(Integer.parseInt((String) postData.get("viewsCount")))
                .build();
    }

    private PostResponseDto getPostFromDatabase(String postId) {
        PostResponseDto post = postRepository.findById(Long.valueOf(postId))
                .map(postMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(postId));
        UserDto author = userServiceClient.getUser(post.authorId());
        cachePostAndAuthor(post, author);
        return post;
    }

    private void cachePosts(List<PostResponseDto> posts) {
        posts.forEach(post -> {
            UserDto author = userServiceClient.getUser(post.authorId());
            if (author != null) {
                cachePostAndAuthor(post, author);
            }
        });
    }

    private void cachePostAndAuthor(PostResponseDto post, UserDto author) {
        String postKey = "post:" + post.id();
        redisTemplate.opsForHash().putAll(postKey, Map.of(
                "content", post.content(),
                "createdAt", post.createdAt().toString(),
                "likesCount", String.valueOf(post.likesCount()),
                "commentsCount", String.valueOf(post.commentsCount()),
                "viewsCount", String.valueOf(post.viewsCount())
        ));
        redisTemplate.expire(postKey, 1, TimeUnit.DAYS);
        String authorKey = "user:" + author.getId();
        redisTemplate.opsForHash().putAll(authorKey, Map.of("username", author.getUsername()));
        redisTemplate.expire(authorKey, 1, TimeUnit.DAYS);
    }

    private FeedResponse buildResponse(List<PostResponseDto> posts) {
        FeedResponse response = FeedResponse.builder().build();
        response.setPosts(posts);
        if (!posts.isEmpty()) {
            response.setLastPostId(String.valueOf(posts.get(posts.size() - 1).id()));
            response.setHasMore(posts.size() >= pageSize);
        }
        return response;
    }
}
