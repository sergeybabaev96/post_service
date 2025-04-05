package faang.school.postservice.config;

import faang.school.postservice.dto.Post.PostEvent;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.model.CommentEvent;
import faang.school.postservice.model.LikeEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.topics.like.name}")
    private String likeTopicName;

    @Value("${spring.kafka.topics.like.partitions}")
    private int partitions;

    @Value("${spring.kafka.topics.like.replicas}")
    private int replicas;

    @Bean(name = "likeTopic")
    public NewTopic likeTopic() {
        return TopicBuilder.name(likeTopicName)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public KafkaTemplate<String, PostEvent> postEventKafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
    }

    @Bean
    public KafkaTemplate<String, PostViewEvent> postViewEventKafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
    }

    @Bean
    public KafkaTemplate<String, Long> authorBunKafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
    }

    @Bean
    public KafkaTemplate<String, LikeEvent> likeEventKafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
    }

    @Bean
    public KafkaTemplate<String, CommentEvent> commentEventKafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostEvent> postEventFactory(
            KafkaProperties kafkaProperties) {
        ConsumerFactory<String, PostEvent> kafkaProjectCreateConsumerFactory =
                new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
        ConcurrentKafkaListenerContainerFactory<String, PostEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaProjectCreateConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}
