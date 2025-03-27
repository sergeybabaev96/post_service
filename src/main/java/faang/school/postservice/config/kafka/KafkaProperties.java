package faang.school.postservice.config.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("spring.kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private int poolSize;
    private Map<String, NewTopic> topics = new HashMap<>();
}
