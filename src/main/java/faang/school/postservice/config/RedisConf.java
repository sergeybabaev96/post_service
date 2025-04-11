package faang.school.postservice.config;

import faang.school.postservice.dto.event.EventDto;
import faang.school.postservice.properties.RedisConnectionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConf {
    private final RedisConnectionProperties redisConnectionProperties;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig =
                new RedisStandaloneConfiguration(redisConnectionProperties.getHost(), redisConnectionProperties.getPort());

        JedisConnectionFactory factory = new JedisConnectionFactory(redisConfig);
        factory.afterPropertiesSet();
        log.info("Created JedisConnectionFactory with host {} and port {}, Redis connection status: {}",
                factory.getHostName(), factory.getPort(), factory.getConnection().isClosed() ? "DISCONNECTION" : "CONNECTION");
        return factory;
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(EventDto.class));
        log.info("Initialized RedisTemplate with EventDto serializer");
        return template;
    }
}

