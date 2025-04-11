package faang.school.postservice.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisConnectionProperties {
    private String host;
    private int port;
    private Map<String, String> topics;

    public String getTopic(TopicKey topicKey) {
        return topics.get(topicKey.key);
    }

    @RequiredArgsConstructor
    @Getter
    public enum TopicKey {
        POST("post"),
        CALCULATIONS("calculations");

        private final String key;
    }
}
