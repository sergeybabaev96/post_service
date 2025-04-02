package faang.school.postservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("post-service.kafka")
public class KafkaProperties {
    private String postTopic;
    private int partitions;
}
