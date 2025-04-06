package faang.school.postservice.service.feed;

import com.google.common.collect.Lists;
import faang.school.postservice.config.redis.FeedProperties;
import faang.school.postservice.event.post.PostCreatedEvent;
import faang.school.postservice.event.post.PostDeletedEvent;
import faang.school.postservice.repository.NewsFeedJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsFeedService {
    private final RedisTemplate<String, String> feedRedisTemplate;
    private final FeedProperties properties;
    private final SaveFeedsAsync saveFeedsAsync;
    private final NewsFeedJdbcRepository newsFeedJdbcRepository;
    private static final String FEED_KEY_PATTERN = "user:%d:feed";
    private static final String POST_FEEDS_INDEX_PATTERN = "post:%d:feeds";
    private static final long SECONDS_IN_A_DAY = 86400L;

    @Value("${spring.data.redis.feed.batch-size}")
    private int batchSize;

    public void addToFeed(PostCreatedEvent event) {
        double score = event.getCreatedAt().toEpochMilli();
        String postId = String.valueOf(event.getPostId());
        String postFeedsKey = String.format(POST_FEEDS_INDEX_PATTERN, event.getPostId());

        List<List<Long>> batches = Lists.partition(event.getFollowerIds(), batchSize);

        for (List<Long> batch : batches) {
            feedRedisTemplate.executePipelined((RedisCallback<Void>) connection -> {
                for (Long userId : batch) {
                    String feedKey = String.format(FEED_KEY_PATTERN, userId);

                    connection.zAdd(feedKey.getBytes(), score, postId.getBytes());
                    connection.zRemRange(feedKey.getBytes(), 0, -properties.getMaxSize() - 1);
                    connection.expire(feedKey.getBytes(), properties.getTtlDays() * SECONDS_IN_A_DAY);

                    connection.sAdd(postFeedsKey.getBytes(), feedKey.getBytes());
                }

                connection.expire(postFeedsKey.getBytes(), properties.getTtlDays() * SECONDS_IN_A_DAY);
                return null;
            });
        }
        saveFeedsAsync.saveFeedsToDb(event);
    }

    public void removeFromFeed(PostDeletedEvent event) {
        String postId = String.valueOf(event.getPostId());
        String postFeedsKey = String.format(POST_FEEDS_INDEX_PATTERN, event.getPostId());
        byte[] postIdBytes = postId.getBytes();

        Set<byte[]> feedKeysBytes = feedRedisTemplate.execute(
                (RedisCallback<Set<byte[]>>) connection -> connection.sMembers(postFeedsKey.getBytes()));

        if (feedKeysBytes == null || feedKeysBytes.isEmpty()) {
            return;
        }

        List<List<byte[]>> batches = Lists.partition(
                new ArrayList<>(feedKeysBytes),
                batchSize
        );

        for (List<byte[]> batch : batches) {
            feedRedisTemplate.executePipelined((RedisCallback<Void>) connection -> {
                for (byte[] feedKeyBytes : batch) {
                    connection.zRem(feedKeyBytes, postIdBytes);
                }
                return null;
            });
        }

        feedRedisTemplate.delete(postFeedsKey);
    }

    public List<Long> getFeed(long userId, int limit) {
        String feedKey = String.format(FEED_KEY_PATTERN, userId);

        if (Boolean.FALSE.equals(feedRedisTemplate.hasKey(feedKey))) {
            CompletableFuture.runAsync(() -> warmUpCache(userId, getColdFeed(userId)));
            return getColdFeed(userId).stream().limit(limit).collect(Collectors.toList());
        }

        return feedRedisTemplate.opsForZSet()
                .reverseRange(feedKey, 0, limit - 1)
                .stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    @Async
    public void warmUpCache(long userId, List<Long> postIds) {
        String feedKey = String.format(FEED_KEY_PATTERN, userId);

        Set<ZSetOperations.TypedTuple<String>> tuples = postIds.stream()
                .map(postId -> new DefaultTypedTuple<>(
                        String.valueOf(postId),
                        (double) postId
                ))
                .collect(Collectors.toSet());

        feedRedisTemplate.opsForZSet().add(feedKey, tuples);
        feedRedisTemplate.expire(feedKey, properties.getTtlDays() * SECONDS_IN_A_DAY, TimeUnit.SECONDS);
    }

    private List<Long> getColdFeed(long userId) {
        return newsFeedJdbcRepository.findColdFeed(userId, 1000);
    }
}