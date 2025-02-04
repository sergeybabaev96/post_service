package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostHashtagCacheService {
    private final PostRepository postRepository;
    private final Jedis jedis;
    private final ObjectMapper objectMapper;

    @Value("${app.cache-live-time}")
    private int CACHE_LIVE_TIME;

    @Transactional
    public void setPostsIntoCache(Post post) {
        List<String> hashtags = post.getHashtags();
        for (String hashtag : hashtags) {
            String cacheKey = "postsBy:" + hashtag;
            Transaction transaction = null;

            try {
                jedis.watch(cacheKey);
                transaction = jedis.multi();

                Response<byte[]> cachedDataResponse = transaction.get(cacheKey.getBytes());

                boolean updated = false;
                List<Post> cachedPosts = new ArrayList<>();

                List<Object> result = transaction.exec();
                if (result == null) {

                    System.out.println("Transaction cancelled for key: " + cacheKey);
                } else {

                    byte[] cachedData = cachedDataResponse.get();

                    if (cachedData != null) {
                        try {
                            cachedPosts = objectMapper.readValue(cachedData, new TypeReference<List<Post>>() {
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

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
                }

                if (result != null) {
                    jedis.setex(cacheKey.getBytes(), CACHE_LIVE_TIME, objectMapper.writeValueAsBytes(cachedPosts));
                }

            } catch (Exception e) {
                if (transaction != null) {
                    try {
                        transaction.discard();
                    } catch (Exception ex) {
                        throw new RuntimeException("Error discarding transaction: " + ex.getMessage(), ex);
                    }
                }
                throw new RuntimeException(e);
            } finally {
                jedis.unwatch();
            }
        }
    }

    @Transactional
    public void removePostFromCache(Post post) {
        List<String> hashtags = post.getHashtags();
        for (String hashtag : hashtags) {
            String cacheKey = "postsBy:" + hashtag;
            Transaction transaction = null;

            try {
                jedis.watch(cacheKey);
                transaction = jedis.multi();
                Response<byte[]> cachedDataResponse = transaction.get(cacheKey.getBytes());

                List<Object> execResult = transaction.exec();

                if (execResult == null) {
                    System.out.println("Transaction cancelled for key: " + cacheKey);
                    continue;
                }

                byte[] cachedData = cachedDataResponse.get();
                List<Post> cachedPosts = new ArrayList<>();

                if (cachedData != null) {
                    try {
                        cachedPosts = objectMapper.readValue(cachedData, new TypeReference<List<Post>>() {
                        });
                    } catch (IOException e) {
                        throw new RuntimeException("Error deserializing cached data: " + e.getMessage(), e);
                    }
                }

                cachedPosts.removeIf(p -> p.getId().equals(post.getId()));

                jedis.setex(cacheKey.getBytes(), CACHE_LIVE_TIME, objectMapper.writeValueAsBytes(cachedPosts));

            } catch (Exception e) {
                if (transaction != null) {
                    try {
                        transaction.discard();
                    } catch (Exception ex) {
                        throw new RuntimeException("Error discarding transaction: " + ex.getMessage(), ex);
                    }
                }
                throw new RuntimeException("Error removing post from cache: " + e.getMessage(), e);
            } finally {
                jedis.unwatch();
            }
        }
    }


    @Transactional(readOnly = true)
    public List<Post> getPostsByHashtag(String hashtag) {
        String cacheKey = "postsBy:" + hashtag;
        List<Post> cachedPosts = getPostsFromCache(cacheKey);

        if (!cachedPosts.isEmpty()) {
            return cachedPosts;
        }

        List<Post> posts = postRepository.findPostsByHashtag(turnIntoJson(hashtag));

        if (posts != null && !posts.isEmpty()) {
            try {
                jedis.setex(cacheKey.getBytes(), CACHE_LIVE_TIME, objectMapper.writeValueAsBytes(posts));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error while serializing posts for cache: " + e.getMessage(), e);
            }
        }

        return posts;
    }

    private List<Post> getPostsFromCache(String cacheKey) {
        byte[] cachedData = jedis.get(cacheKey.getBytes());
        List<Post> cachedPosts = new ArrayList<>();
        if (cachedData != null) {
            try {
                cachedPosts = objectMapper.readValue(cachedData, new TypeReference<List<Post>>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error deserializing from cache: " + e.getMessage(), e);
            }
        }
        return cachedPosts;
    }

    private static String turnIntoJson(String hashtag) {
        return "[\"" + hashtag + "\"]";
    }
}