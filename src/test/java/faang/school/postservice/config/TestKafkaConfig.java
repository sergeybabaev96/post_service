package faang.school.postservice.config;

import faang.school.postservice.model.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration
@RequiredArgsConstructor
public class TestKafkaConfig {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LikeEvent> likeEventTestFactory(
            KafkaProperties kafkaProperties) {
        ConsumerFactory<String, LikeEvent> kafkaProjectCreateConsumerFactory =
                new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
        ConcurrentKafkaListenerContainerFactory<String, LikeEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaProjectCreateConsumerFactory);
        return factory;
    }
}