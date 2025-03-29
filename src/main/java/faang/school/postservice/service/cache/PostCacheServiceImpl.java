package faang.school.postservice.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheServiceImpl implements PostCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public void cachePost(PostResponseDto post) {
        String value;
        try {
            value = objectMapper.writeValueAsString(post);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.opsForValue().set("posts::" + post.id(), value, Duration.ofHours(24)
        );
    }

    public Optional<PostResponseDto> getCachedPost(long postId) {
        try {
            String cacheKey = String.format("%s::%d", "posts", postId);
            Optional<String> post = Optional.ofNullable(redisTemplate.opsForValue().get(cacheKey));
            if (post.isPresent()) {
                return Optional.ofNullable(objectMapper.readValue(post.get(), PostResponseDto.class));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize cached post with id: {}", postId, e);
            redisTemplate.delete(String.format("%s::%d", "posts", postId));
            log.error("Invalid cache post deleted");
        }
        return Optional.empty();
    }

}
