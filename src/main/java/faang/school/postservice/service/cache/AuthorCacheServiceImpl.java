package faang.school.postservice.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorCacheServiceImpl implements AuthorCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private final String authorsCacheKey = "authors";

    public void cacheAuthor(Long authorId, UserDto author) {
        try {
            String authorJson = objectMapper.writeValueAsString(author);
            redisTemplate.opsForHash().put(
                    authorsCacheKey,
                    authorId.toString(),
                    authorJson
            );
            redisTemplate.expire(authorsCacheKey, 10000, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize author for caching, id: {}", authorId, e);
        }
    }

    public Optional<UserDto> getCachedAuthor(Long authorId) {
        try {
            Object authorJson = redisTemplate.opsForHash().get(authorsCacheKey, authorId.toString());
            if (authorJson == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(
                    objectMapper.readValue(authorJson.toString(), UserDto.class)
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize cached author with id: {}", authorId, e);
            return Optional.empty();
        }
    }
}