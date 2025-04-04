package faang.school.postservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostRedisDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class PostCacheRepository {
    private final RedisTemplate<String, Object> postRedisTemplate;
    private final PostMapper postMapper;
    private final ObjectMapper objectMapper;
    @Value("${spring.data.redis.post-ttl-hours}")
    private int ttlInHours;
    @Value("${spring.data.redis.post-id-key}")
    private String postKey;


    public void save(Post post) {
        PostRedisDto dto = postMapper.toRedisDto(post);
        postRedisTemplate.opsForValue().set(getKeyString(dto.id()), dto, ttlInHours, TimeUnit.HOURS);
    }

    public void saveAll(List<Post> postList) {
        postList.forEach(this::save);
    }

    @Nullable
    public Post findById(Long id) {
        Object object = postRedisTemplate.opsForValue().get(getKeyString(id));
        PostRedisDto dto = objectMapper.convertValue(object, PostRedisDto.class);
        return postMapper.toEntityFromRedis(dto);
    }

    public void deletePost(Long id) {
        postRedisTemplate.delete(getKeyString(id));
    }

    private String getKeyString(Long id) {
        return "%s_%d".formatted(postKey, id);
    }
}
