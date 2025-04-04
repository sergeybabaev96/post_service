package faang.school.postservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicRegistry implements TopicConfigurer {
    private final KafkaAdmin kafkaAdmin;

    @Value("${spring.data.kafka.comments.channels}")
    private String commentsTopic;

    @Value("${spring.data.kafka.comments.partitions}")
    private int commentsPartitions;

    @Value("${spring.data.kafka.comments.replication-factor}")
    private int commentsReplicationFactor;

    @Override
    @Bean
    public void configureTopics() {
        List<NewTopic> topics = List.of(
                new NewTopic(commentsTopic, commentsPartitions, (short) commentsReplicationFactor)
        );
        kafkaAdmin.createOrModifyTopics(topics.toArray(new NewTopic[0]));
    }
}
