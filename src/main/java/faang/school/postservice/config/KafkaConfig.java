package faang.school.postservice.config;

import faang.school.postservice.model.CommentEvent;
import lombok.RequiredArgsConstructor;
import faang.school.postservice.model.LikeEvent;
import faang.school.postservice.model.PostEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    @Value("${spring.kafka.topics.like.name}")
    private String likeTopicName;

    @Value("${spring.kafka.topics.like.partitions}")
    private int partitions;

    @Value("${spring.kafka.topics.like.replicas}")
    private int replicas;

    private final Environment environment;

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
    public KafkaTemplate<String, Long> authorBunKafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
    }

    @Bean
    public KafkaTemplate<String, LikeEvent> likeEventKafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CommentEvent> commentContainerFactory(
            KafkaProperties kafkaProperties) {
        ConsumerFactory<String, CommentEvent> kafkaProjectViewConsumerFactory =
                getConsumerFactory(kafkaProperties);
        ConcurrentKafkaListenerContainerFactory<String, CommentEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaProjectViewConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    private ConsumerFactory<String, CommentEvent> getConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                environment.getProperty("spring.kafka.groups.comment.name"));

        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(CommentEvent.class));
    }
}
