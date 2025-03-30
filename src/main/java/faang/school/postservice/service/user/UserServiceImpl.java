package faang.school.postservice.service.user;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserResponseDto;
import faang.school.postservice.exception.DataFetchException;
import faang.school.postservice.mapper.user.UserDtoAdapter;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    //private final UserRedisRepository userRedisRepository;
    private final UserServiceClient userServiceClient;
    private final UserDtoAdapter userMapper;
    private final RedisTemplate <String, UserResponseDto> userRedisTemplate;
    private final RedisProperties redisProperties;


    @Override
    public UserDto getUserWithCache(long userId) {

        String cacheKey = redisProperties.cache().userCacheName() + userId;
        UserResponseDto cachedUser = userRedisTemplate.opsForValue().get(cacheKey);

        if (cachedUser != null) {
            log.info("Returning cached user for id: {}", userId);
            return userMapper.toUserDto(cachedUser);
        }

        // 2. Если нет в кеше - запрос через Feign
        log.info("Cache miss for user id: {}. Fetching from external service...", userId);
        try {
            UserResponseDto userResponseDto = userServiceClient.getUser(userId);

            if (userResponseDto != null) {

                // 3. Сохранение в кеш
                userRedisTemplate.opsForValue().set(
                        cacheKey,
                        userResponseDto,
                        redisProperties.cache().userTtlMinutes(),
                        TimeUnit.MINUTES
                );

            }
            return userMapper.toUserDto(userResponseDto);
        } catch (FeignException e) {
            log.error("Error fetching data from external service for ID: {}", userId, e);
            throw new DataFetchException("Failed to fetch user with id: " + userId);
        }


        /*
        Optional<User> cachedUser = userRedisRepository.findById(userId);

        if (cachedUser.isPresent()) {
            log.info("User {} get from cache", userId);
            return userMapper.toUserDto(cachedUser.get());
        }
        UserResponseDto userResponseDto = userServiceClient.getUser(userId);
        log.info("User {} get from user service", userId);

        User user = userMapper.toUser(userResponseDto);

        if (user != null) {
            userRedisRepository.save(user);
            log.info("User {} updated in cache", userId);
        }
        return user != null ? userMapper.toUserDto(userResponseDto) : null;*/

    }
}
