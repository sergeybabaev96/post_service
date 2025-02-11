package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.mapper.user.UserMapper;
import faang.school.postservice.model.redis.FeedCache;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.model.redis.UserCache;
import faang.school.postservice.repository.redis.FeedCacheRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
import faang.school.postservice.repository.redis.UserCacheRepository;
import faang.school.postservice.service.post.PostService;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

  @Value("${newsfeed.posts.limit}")
  private int postsLimit;

  @Value("${newsfeed.posts.clean}")
  private int postsToDrop;

  private final FeedCacheRepository feedCacheRepository;
  private final UserCacheRepository userCacheRepository;
  private final PostCacheRepository postCacheRepository;
  private final UserServiceClient userServiceClient;
  private final PostService postService;
  private final UserMapper userMapper;
  private final PostMapper postMapper;

  //TODO async processing each follower's feed
  @Override
  @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
  public void processPostEvent(PostEventDto dto) {
    List<Long> followers = dto.getFollowers(); // list of users to update theirs feeds

    Long postId = dto.getPosId();

    followers.forEach(user -> updateUserFeed(user, postId));

//    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> ...);
  }

  @Override
  public void updateUserFeed(Long userId, Long postId) {
    FeedCache testFeed = getCachedFeed(userId);

    LinkedHashSet<Long> posts = testFeed.getPostsIds();

    testFeed.addPost(postId);

    if (posts.size() > postsLimit) {
      for (int i = 0; i < postsToDrop; i++) {
        posts.remove(posts.iterator().next());
      }
    }
    feedCacheRepository.save(testFeed);
  }

  public FeedCache getCachedFeed(Long userId) {
    return feedCacheRepository.findById(userId)
        .orElseGet(() ->
            FeedCache.builder()
                .id(userId)
                .postsIds(new LinkedHashSet<>())
                .build()
        );
  }

  private FeedCache getCachedFeed(Long userId, Long previousPostId, int pageSize) {
    FeedCache feed = feedCacheRepository.findById(userId)
        .orElseGet(() ->
            FeedCache.builder()
                .id(userId)
                .postsIds(new LinkedHashSet<>())
                .build()
        );

    LinkedHashSet<Long> postsIds = feed.getPostsIds();

    if (!postsIds.isEmpty()) {

      List<Long> postsIdsList = List.copyOf(postsIds);

      int toIndex;

      if (previousPostId == null) {

        toIndex = postsIdsList.size();

      } else {

        toIndex = postsIdsList.indexOf(previousPostId);

        if (toIndex == -1) {
          throw new IllegalArgumentException("previous last post not found");

        }
      }

      int fromIndex = Math.max(0, toIndex - pageSize);

      List<Long> postsIdsToGetList = postsIdsList.subList(fromIndex, toIndex).stream().sorted(
          Comparator.reverseOrder()).toList();

      LinkedHashSet<Long> postsToGet = new LinkedHashSet<>(postsIdsToGetList);

      feed.setPostsIds(postsToGet);
    }
    return feed;
  }

  @Override
  public FeedDto getFeed(Long userId, Long previousPostId, int pageSize) {
    return mapToFeedDto(getCachedFeed(userId, previousPostId, pageSize));
  }

  private FeedDto mapToFeedDto(FeedCache feedCache) {
    long userId = feedCache.getId();
    UserCache userCache = userCacheRepository.findById(userId)
        .orElseGet(() -> getUserFromDB(userId));
    //TODO - и еще надо в кэш сразу добавить полученного из БД юзера (когда из БД берем)

    log.info("getting user: {}", userCache.getUsername());

    List<PostCache> posts = feedCache.getPostsIds().stream()
        .map(postId -> postCacheRepository.findById(postId)
            .orElseGet(() -> getPostFromDB(postId)))
        .toList();
    //TODO - и еще надо в кэш сразу добавить полученные из БД посты (когда из БД берем)

    return FeedDto.builder()
        .user(userCache)
        .posts(posts)
        .build();
  }

  private UserCache getUserFromDB(Long userId) {
    return userMapper.toUserCache(userServiceClient.getUser(userId));
  }

  private PostCache getPostFromDB(Long postId) {
    return postMapper.toPostCache(postService.findPostById(postId));
  }

}
