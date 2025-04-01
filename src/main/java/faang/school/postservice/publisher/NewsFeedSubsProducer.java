package faang.school.postservice.publisher;

import faang.school.postservice.event.NewsFeedSubEvent;
import faang.school.postservice.event.PostEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class NewsFeedSubsProducer extends MessagePublisher<NewsFeedSubEvent> {

    public NewsFeedSubsProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${spring.kafka.topics.news-feed-subs.name}") String topic
    ) {
        super(kafkaTemplate, topic);
    }

    @Override
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void publish(NewsFeedSubEvent message) {
        kafkaTemplate.send(topic, message).join();
    }
}
