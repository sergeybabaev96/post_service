package faang.school.postservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisConfiguration {
    @Value("${spring.data.redis.host}")
    private String redisHostName;

    @Value("${spring.data.redis.port}")
    private Integer redisPort;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        // redisConfig.setHostName(redisHostName);
        // redisConfig.setPort(redisPort);
        return new JedisConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Long> redisTemplate() {
        RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // redisTemplate.setHashKeySerializer(new JdkSerializationRedisSerializer());
        // redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        // redisTemplate.setEnableTransactionSupport(true);
        // redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
