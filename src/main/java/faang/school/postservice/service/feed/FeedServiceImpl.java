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
import java.util.ArrayList;
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

  // нужен утильный метод с дженериком
  private List<List<Long>> splitIntoBatches(List<Long> followers) {
    int batchSize = 1_000_000; //hardcoded - вынести в параметры тоже
    int totalSize = followers.size();
    int batchNumbs = (totalSize + batchSize - 1) / batchSize;
    List<List<Long>> batches = new ArrayList<>();
    for (int i = 0; i < batchNumbs; i++) {
      int start = i * batchSize;
      int end = Math.min(totalSize, (i + 1) * batchSize);
      batches.add(followers.subList(start, end));
    }
    return batches;
  }

  //TODO synchronize block? refresh
  @Override
  public void updateUserFeed(Long userId, Long postId) {
    FeedCache feed = getCachedFeedAllPosts(userId);

    LinkedHashSet<Long> postsIds = feed.getPostsIds();
// здесб оптимистик лок должен отраббатывать, вероятно что-то типа флага придется делать, если нет в редисе аннотации
    feed.addPost(postId);
//TODO reviewer help: won't work in multithread environment (check other methods)
    // надо реалищовать сначала многопоточку, тогда наглядно будет видно
    // когда асинхронно обрабатываем в разных потоках прилетевшие из кафки события
    // по ним будет отработка фида одного пользователя (от разных постов)
    // + еще здесь параллельно идет по каждому посту, куча потоков
    if (postsIds.size() > postsLimit) {
      for (int i = 0; i < postsToDrop; i++) {
        postsIds.remove(postsIds.iterator().next());
      }
    }
    feedCacheRepository.save(feed);
  }

  @Override
  public FeedDto getFeed(Long userId, Long previousPostId, int pageSize) {
    return mapToFeedDto(getCachedFeed(userId, previousPostId, pageSize));
  }

  private FeedCache getCachedFeed(Long userId, Long previousPostId, int pageSize) {
    FeedCache feed = getCachedFeedAllPosts(userId);

    LinkedHashSet<Long> allPostsIds = feed.getPostsIds();

    if (allPostsIds == null || allPostsIds.isEmpty()) {
      allPostsIds = postService.getUserFeed(userId, postsLimit);

      if (allPostsIds != null) {
        feed.setPostsIds(allPostsIds);
        feedCacheRepository.save(feed);
      } else {
        return feed;
      }
    }

    LinkedHashSet<Long> onePagePostsIds = getPostsIds(previousPostId, pageSize,
        allPostsIds);

    feed.setPostsIds(onePagePostsIds);

    return feed;
  }

  private static LinkedHashSet<Long> getPostsIds(Long previousPostId, int pageSize,
      LinkedHashSet<Long> postsIds) {
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

    return new LinkedHashSet<>(postsIdsToGetList);
  }

  private FeedDto mapToFeedDto(FeedCache feedCache) {
    long userId = feedCache.getId();
    UserCache userCache = userCacheRepository.findById(userId)
        .orElseGet(() -> userCacheRepository.save(getUserFromDB(userId)));

    log.info("getting user: {}", userCache.getUsername());

    List<PostCache> posts = feedCache.getPostsIds().stream()
        .map(postId -> postCacheRepository.findById(postId)
            .orElseGet(() -> postCacheRepository.save(getPostFromDB(postId))))
        .toList();

    return FeedDto.builder()
        .user(userCache)
        .posts(posts)
        .build();
  }

  private UserCache getUserFromDB(Long userId) {
    return userMapper.toUserCache(userServiceClient.getUser(userId));
  }

  private PostCache getPostFromDB(Long postId) {
    PostCache postCache = postMapper.toPostCache(postService.findPostById(postId));
    postCache.setAuthorName(userServiceClient.getUser(postCache.getAuthorId()).getUsername());
    return postCache;
  }

  private FeedCache getCachedFeedAllPosts(Long userId) {
    LinkedHashSet<Long> set = new LinkedHashSet<>();
    return feedCacheRepository.findById(userId)
        .orElseGet(() ->
            FeedCache.builder()
                .id(userId)
                .postsIds(set)
                .build()
        );
  }

}
