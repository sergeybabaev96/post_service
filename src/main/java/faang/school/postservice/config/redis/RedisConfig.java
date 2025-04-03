package faang.school.postservice.config.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

@Getter
@Configuration
@ConfigurationProperties(prefix = "data.redis")
@RequiredArgsConstructor(onConstructor_ = @ConstructorBinding)
public class RedisConfig {
    private final int port;
    private final String host;
    private final Map<String, Channel> channels;

    public record Channel(String name) {
    }

    @Bean
    @Qualifier("user-ban")
    public RedisTemplate<String, Long> userBanRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    @Qualifier("user-ban")
    ChannelTopic userBanTopic() {
        return new ChannelTopic(channels.get("user_ban").name());
    }
}
