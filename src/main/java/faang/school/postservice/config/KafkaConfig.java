package faang.school.postservice.config;

import faang.school.postservice.properties.KafkaProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic postEventsTopic(KafkaProperties kafkaProperties) {
        return TopicBuilder.name(kafkaProperties.getPostTopic())
                .partitions(kafkaProperties.getPartitions())
                .replicas(1)
                .build();
    }
}
