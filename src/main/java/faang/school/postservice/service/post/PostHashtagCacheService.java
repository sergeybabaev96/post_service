package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostHashtagCacheService {
    private final PostRepository postRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.cache-live-time}")
    private int CACHE_LIVE_TIME;

    @Transactional(readOnly = true)
    public List<Post> getPostsByHashtag(String hashtag) {
        String cacheKey = "postsBy:" + hashtag;
        List<Post> cachedPosts = (List<Post>) redisTemplate.opsForValue().get(cacheKey);

        if (cachedPosts != null) {
            return cachedPosts;
        }

        String str = "[\"" + hashtag + "\"]";
        List<Post> posts = postRepository.findPostsByHashtag(str);

        redisTemplate.opsForValue().set(cacheKey, posts);

        return posts;
    }

    public void updatePostsInCache(Post post) {
        List<String> hashtags = post.getHashtags();
        for (String hashtag : hashtags) {
            String cacheKey = "postsBy:" + hashtag;

            redisTemplate.execute((RedisCallback<Object>) redisConnection -> {
                redisConnection.multi();

                List<Post> cachedPosts = getCachedPosts(redisConnection, cacheKey);

                boolean updated = false;
                for (Post cachedPost : cachedPosts) {
                    if (cachedPost.getId().equals(post.getId())) {
                        cachedPosts.set(cachedPosts.indexOf(cachedPost), post);
                        updated = true;
                        break;
                    }
                }

                if (!updated) {
                    cachedPosts.add(post);
                }

                updatePostsInCache(redisConnection, cacheKey, cachedPosts);

                return redisConnection.exec();
            });
        }
    }

    public void removePostFromCache(Post post) {
        List<String> hashtags = post.getHashtags();
        for (String hashtag : hashtags) {
            String cacheKey = "postsBy:" + hashtag;

            redisTemplate.execute((RedisCallback<Object>) redisConnection -> {
                redisConnection.multi();

                List<Post> cachedPosts = getCachedPosts(redisConnection, cacheKey);

                cachedPosts.removeIf(cachedPost -> cachedPost.getId().equals(post.getId()));

                updatePostsInCache(redisConnection, cacheKey, cachedPosts);

                return redisConnection.exec();
            });
        }
    }

    private void updatePostsInCache(RedisConnection redisConnection, String cacheKey, List<Post> cachedPosts) {
        redisConnection.del(cacheKey.getBytes());
        try {
            redisConnection.setEx(cacheKey.getBytes(), CACHE_LIVE_TIME, objectMapper.writeValueAsBytes(cachedPosts));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Post> getCachedPosts(RedisConnection redisConnection, String cacheKey) {
        byte[] cachedData = redisConnection.get(cacheKey.getBytes());
        List<Post> cachedPosts = new ArrayList<>();
        if (cachedData != null) {
            try {
                cachedPosts = objectMapper.readValue(cachedData, new TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return cachedPosts;
    }
}