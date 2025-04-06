package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.producer.topic.post.replicas}")
    private int replicas;

    @Value("${spring.kafka.producer.topic.post.partitions}")
    private int partitions;
    @Bean
    public NewTopic postsTopic() {
        return TopicBuilder.name("posts")
                .partitions(partitions)
                .replicas(partitions)
                .build();
    }
}
